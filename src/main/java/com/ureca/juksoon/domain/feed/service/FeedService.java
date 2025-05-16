package com.ureca.juksoon.domain.feed.service;

import com.ureca.juksoon.domain.common.FileType;
import com.ureca.juksoon.domain.feed.dto.request.CreateFeedReq;
import com.ureca.juksoon.domain.feed.dto.request.ModifyFeedReq;
import com.ureca.juksoon.domain.feed.dto.response.CreateFeedRes;
import com.ureca.juksoon.domain.feed.dto.response.DeleteFeedRes;
import com.ureca.juksoon.domain.feed.dto.response.GetFeedDetailRes;
import com.ureca.juksoon.domain.feed.dto.response.GetFeedRes;
import com.ureca.juksoon.domain.feed.dto.response.GetHomeInfoRes;
import com.ureca.juksoon.domain.feed.dto.response.GetMypageInfoRes;
import com.ureca.juksoon.domain.feed.dto.response.ModifyFeedRes;
import com.ureca.juksoon.domain.feed.entity.Category;
import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.FeedFile;
import com.ureca.juksoon.domain.feed.entity.SortType;
import com.ureca.juksoon.domain.feed.repository.FeedFileRepository;
import com.ureca.juksoon.domain.feed.repository.FeedRepository;
import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.store.repository.StoreRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.event.event.CreationFeedEvent;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.s3.FilePath;
import com.ureca.juksoon.global.s3.S3Service;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.ureca.juksoon.global.response.ResultCode.FEED_NOT_FOUND;
import static com.ureca.juksoon.global.response.ResultCode.FORBIDDEN;
import static com.ureca.juksoon.global.response.ResultCode.STORE_NOT_FOUND;
import static com.ureca.juksoon.global.response.ResultCode.USER_NOT_FOUNT;

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
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Home 조회
     */
    @Transactional(readOnly = true)
    public GetHomeInfoRes getHomeInfo(Pageable pageable, String keyword, Category category, boolean isAvailable, SortType sortType) {
        long maxPage = (feedRepository.countAllByFiltering(isAvailable, category, keyword) + pageable.getPageSize() - 1) / pageable.getPageSize();

        return new GetHomeInfoRes(maxPage, feedRepository.findPageByFiltering(pageable, isAvailable, sortType, category, keyword).stream()
                .map(GetFeedRes::new).toList());
    }

    /**
     * Mypage 조회
     */
    @Transactional(readOnly = true)
    public GetMypageInfoRes getMypageInfo(Long userId, Pageable pageable, Long lastFeedId) {
        User user = findUser(userId);

        if (user.getRole() == UserRole.ROLE_TESTER) { // 일반 사용자
            // Reservation 기반 조회
            List<GetFeedRes> feedResList = new ArrayList<>(feedRepository.findAllByUserOrderByFeedIdDesc(pageable, user, lastFeedId).stream()
                    .map(GetFeedRes::new).toList());

            // 다음 페이지 확인 및 반환값 조정
            boolean hasNextPage = (feedResList.size() > pageable.getPageSize());
            if (hasNextPage) feedResList.remove(feedResList.size() - 1);

            return new GetMypageInfoRes(user.getId(), user.getNickname(), user.getRole(), hasNextPage, feedResList);
        } else if (user.getRole() == UserRole.ROLE_OWNER) { // 사장님
            Store store = findStoreByUserId(user.getId());

            // store 기반 조회
            List<GetFeedRes> feedResList = new ArrayList<>(feedRepository.findAllByStoreOrderByFeedIdDesc(pageable, store, lastFeedId).stream()
                    .map(GetFeedRes::new).toList());

            // 다음 페이지 확인 및 반환값 조정
            boolean hasNextPage = (feedResList.size() > pageable.getPageSize());
            if (hasNextPage) feedResList.remove(feedResList.size() - 1);

            return new GetMypageInfoRes(store.getId(), store.getName(), user.getRole(), hasNextPage, feedResList);
        }

        // 이외의 경우에는 myPage 접근 불가
        throw new GlobalException(FORBIDDEN);
    }

    /**
     * Feed 단일 조회
     */
    @Transactional(readOnly = true)
    public GetFeedDetailRes getFeedDetail(Long feedId) {
        Feed feed = findFeed(feedId);
        List<FeedFile> files = feedFileRepository.findAllByFeed(feed);

        // 이미지 파일 분리
        List<String> imageUrlList = new ArrayList<>();
        String videoUrl = null;
        for (FeedFile file : files) {
            if (file.getType() == FileType.IMAGE) {
                imageUrlList.add(file.getUrl());
            } else {
                videoUrl = file.getUrl();
            }
        }
        return new GetFeedDetailRes(feed, (imageUrlList.isEmpty() ? null : imageUrlList), videoUrl);
    }

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

        //@TransactionalEventListener로 트랜잭션 실행 후 레디스 실행
        makeTicketPublisherInRedis(feed);

        // 이미지 & 비디오: s3 업로드 및 DB 저장
        saveFeedFiles(req.getImages(), req.getVideo(), savedFeed);

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

        // feedFile 제거
        if (!files.isEmpty()) {
            s3Service.deleteMultiFiles(files.stream().map(FeedFile::getUrl).toList(), FilePath.Feed);
            feedFileRepository.deleteAll(files);
        }

        // feed 제거
        feedRepository.delete(feed);

        return new DeleteFeedRes();
    }

    /**
     * Feed 수정
     */
    @Transactional
    public ModifyFeedRes modifyFeed(Long userId, Long feedId, ModifyFeedReq req) {
        // 권한 확인 : 본인이 작성한 피드만 삭제 가능
        User user = findUser(userId);
        Feed feed = findFeed(feedId);

        checkAuthority(user, feed);

        // feed 정보 갱신
        feed.update(req);

        // 기존 feedFile 정보 제거
        List<FeedFile> files = feedFileRepository.findAllByFeed(feed);

        s3Service.deleteMultiFiles(files.stream().map(FeedFile::getUrl).toList(), FilePath.Feed);
        feedFileRepository.deleteAll(files);

        // 새로운 feedFile 정보 추가
        saveFeedFiles(req.getImages(), req.getVideo(), feed);

        return new ModifyFeedRes();
    }

    //피드 상태 전환 활성 => 비활성
    @Transactional
    public void deactivateFeedByScheduler() {
        feedRepository.deactivateAllStatus();
    }

    //피드 상태 전환 비활성 => 활성
    @Transactional
    public void activateFeedByScheduler() {
        feedRepository.activateAllStatus();
    }

    private void saveFeedFiles(List<MultipartFile> images, MultipartFile video, Feed feed) {
        List<FeedFile> fileList = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                fileList.add(FeedFile.of(feed, s3Service.uploadFile(image, FilePath.Feed), FileType.IMAGE));
            }
        }

        if (video != null && !video.isEmpty()) { // VIDEO가 존재하면 저장
            fileList.add(FeedFile.of(feed, s3Service.uploadFile(video, FilePath.Feed), FileType.VIDEO));
        }
        feedFileRepository.saveAllFeedFiles(fileList);
    }

    private void checkAuthority(User user, Feed feed) {
        if (!feed.getUser().getId().equals(user.getId())) {
            throw new GlobalException(FORBIDDEN); // 사용자 본인의 피드인지 확인
        }
    }

    private Store findStoreByUserId(Long userId) {
        Store store = storeRepository.findByUserId(userId);
        if (store == null) throw new GlobalException(STORE_NOT_FOUND);
        return store;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new GlobalException(USER_NOT_FOUNT));
    }

    private Feed findFeed(Long feedId) {
        return feedRepository.findById(feedId).orElseThrow(() -> new GlobalException(FEED_NOT_FOUND));
    }

    private void makeTicketPublisherInRedis(Feed feed) {
        applicationEventPublisher.publishEvent(new CreationFeedEvent(
                feed.getId(),
                feed.getRegisteredUser(),
                feed.getMaxUser(),
                feed.getExpiredAt()
        ));
    }
}
