package com.ureca.juksoon.domain.feed.service;

import com.ureca.juksoon.domain.feed.dto.request.CreateFeedReq;
import com.ureca.juksoon.domain.feed.dto.responce.CreateFeedRes;
import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.FeedFile;
import com.ureca.juksoon.domain.feed.entity.FileType;
import com.ureca.juksoon.domain.feed.repository.FeedFileRepository;
import com.ureca.juksoon.domain.feed.repository.FeedRepository;
import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.store.repository.StoreRepository;
import com.ureca.juksoon.domain.user.entity.User;
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

import static com.ureca.juksoon.global.response.ResultCode.STORE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final EntityManager em;
    private final S3Service s3Service;
    private final FeedRepository feedRepository;
    private final FeedFileRepository feedFileRepository;
    private final StoreRepository storeRepository;

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

    private Store findStoreByUserId(Long userId) {
        Store store = storeRepository.findByUserId(userId);
        if(store == null) throw new GlobalException(STORE_NOT_FOUND);
        return store;
    }
}
