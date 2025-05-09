package com.ureca.juksoon.domain.feed.service;

import com.ureca.juksoon.domain.feed.dto.request.CreateFeedReq;
import com.ureca.juksoon.domain.feed.dto.responce.CreateFeedRes;
import com.ureca.juksoon.domain.feed.dto.responce.DeleteFeedRes;
import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.FeedFile;
import com.ureca.juksoon.domain.feed.entity.FileType;
import com.ureca.juksoon.domain.feed.repository.FeedFileRepository;
import com.ureca.juksoon.domain.feed.repository.FeedRepository;
import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.store.repository.StoreRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.s3.FilePath;
import com.ureca.juksoon.global.s3.S3Service;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.ureca.juksoon.global.response.ResultCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final EntityManager em;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final FeedRepository feedRepository;
    private final FeedFileRepository feedFileRepository;

    /**
     * Feed 생성
     */
    @Transactional
    public CreateFeedRes createFeed(Long userId, CreateFeedReq req) {

        // Feed 생성
        User user = em.getReference(User.class, userId);
        Store store = findStoreByUserId(userId);
        Feed feed = Feed.of(req, user, store);

        Feed savedFeed = feedRepository.save(feed);

        // 이미지 & 비디오: s3 업로드 및 DB 저장
        List<FeedFile> fileList = new ArrayList<>();

        if (req.getImages() != null && !req.getImages().isEmpty()) {
            for (MultipartFile image : req.getImages()) {
                fileList.add(FeedFile.of(feed, s3Service.uploadFile(image, FilePath.Feed), FileType.IMAGE));
            }
        }

        if(req.getVideo() != null && !req.getVideo().isEmpty()) { // VIDEO가 존재하면 저장
            fileList.add(FeedFile.of(feed, s3Service.uploadFile(req.getVideo(), FilePath.Feed), FileType.VIDEO));
        }

        feedFileRepository.saveAll(fileList);

        return new CreateFeedRes(savedFeed.getId());
    }

    /**
     * Feed 삭제
     */
    @Transactional
    public DeleteFeedRes deleteFeed(Long userId, Long feedId) {
        // 권한 확인 : 본인이 작성한 피드만 삭제 가능
        User user = findUser(userId);
        Feed feed = findFeed(feedId);

        checkAuthority(user, feed);

        List<FeedFile> files = feedFileRepository.findAllByFeed(feed);

        // S3에서 제거
        for (FeedFile file : files) {
            s3Service.deleteFile(file.getUrl(), FilePath.Feed);
        }

        // DB 정보 제거
        feedFileRepository.deleteAll(files);
        feedRepository.delete(feed);

        return new DeleteFeedRes();
    }

    private void checkAuthority(User user, Feed feed) {
        if(!feed.getUser().getId().equals(user.getId())) {
            throw new GlobalException(FORBIDDEN); // 사용자 본인의 피드인지 확인
        }
    }

    private Store findStoreByUserId(Long userId) {
        Store store = storeRepository.findByUserId(userId);
        if(store == null) throw new GlobalException(STORE_NOT_FOUND);
        return store;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new GlobalException(USER_NOT_FOUNT));
    }

    private Feed findFeed(Long feedId) {
        return feedRepository.findById(feedId).orElseThrow(() -> new GlobalException(FEED_NOT_FOUND));
    }
}
