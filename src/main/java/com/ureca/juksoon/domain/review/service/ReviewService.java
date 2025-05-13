package com.ureca.juksoon.domain.review.service;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.repository.FeedRepository;
import com.ureca.juksoon.domain.review.dto.ReviewWithFiles;
import com.ureca.juksoon.domain.review.dto.request.ReviewReq;
import com.ureca.juksoon.domain.review.dto.response.CreateReviewRes;
import com.ureca.juksoon.domain.review.dto.response.DeleteReviewRes;
import com.ureca.juksoon.domain.review.dto.response.GetReviewsRes;
import com.ureca.juksoon.domain.review.dto.response.ModifyReviewRes;
import com.ureca.juksoon.domain.review.entity.FileType;
import com.ureca.juksoon.domain.review.entity.Review;
import com.ureca.juksoon.domain.review.entity.ReviewFile;
import com.ureca.juksoon.domain.review.repository.ReviewFileRepository;
import com.ureca.juksoon.domain.review.repository.ReviewRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.s3.FilePath;
import com.ureca.juksoon.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ureca.juksoon.global.response.ResultCode.REVIEW_NOT_FOUND;
import static com.ureca.juksoon.global.response.ResultCode.USER_NOT_FOUNT;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewFileRepository reviewFileRepository;
    private final S3Service s3Service;


    /**
     * review 생성
     */
    @Transactional
    public CreateReviewRes createReview(Long userId, Long feedId, ReviewReq req){

        User user = userRepository.getReferenceById(userId);
        Feed feed = feedRepository.getReferenceById(feedId);
        Review review = Review.of(user, feed, req);

        Review saveReview = reviewRepository.save(review);
        saveReviewFiles(req.getImages(), req.getVideo(), saveReview);

        return new CreateReviewRes(saveReview.getId());
    }


    /**
     * review 가져오기
     * - Role에 따라 하나 or 다수
     * - review와 reviewfile을 가져와 하나의 dto로 반환
     */
    public GetReviewsRes getReviews(Long userId, UserRole userRole, Long feedId) {

        log.info("사용자 권한: {}", userRole);

        // 리뷰에 해당하는 파일들 모두 조회
        List<Review> reviews = List.of();

        if(userRole.equals(UserRole.ROLE_OWNER)){
            reviews = reviewRepository.findAllByFeedId(feedId);
        }

        if(userRole.equals(UserRole.ROLE_TESTER)){
            reviews = reviewRepository.findByFeedIdAndUserId(feedId, userId)
                    .map(List::of)
                    .orElse(List.of());
        }

        // 리뷰 작성자 id 받아오기
        List<Long> reviewIds = reviews.stream()
                .map(Review::getId)
                .toList();

        // 리뷰에 연결된 파일 전부 조회
        List<ReviewFile> reviewFiles = reviewFileRepository.findAllByReview_IdIn(reviewIds);

        // 리뷰 ID 기준으로 파일 리뷰-파일 연결
        Map<Long, List<ReviewFile>> fileMap = reviewFiles.stream()
                .collect(Collectors.groupingBy(rf -> rf.getReview().getId()));

        // DTO 변환
        List<ReviewWithFiles> reviewWithFiles = reviews.stream()
                .map(review -> ReviewWithFiles.from(
                        review, fileMap.getOrDefault(review.getId(), List.of())))
                .toList();

        return new GetReviewsRes(reviewWithFiles);
    }


    /**
     * review 수정
     * - TESTER만 가능 (OWNER는 X)
     */
    @Transactional
    public ModifyReviewRes updateReview(Long userId, Long feedId, ReviewReq req) {
        // review 정보 갱신, user가 작성한 리뷰 찾기
        Review review = reviewRepository.findByFeedIdAndUserId(feedId, userId)
                .orElseThrow(() -> new GlobalException(REVIEW_NOT_FOUND));
        review.update(req);

        // 기존 review 파일 S3에서 제거
        List<ReviewFile> files = reviewFileRepository.findAllByReviewId(review.getId());
        s3Service.deleteMultiFiles(files.stream().map(ReviewFile::getUrl).toList(), FilePath.REVIEW);
        reviewFileRepository.deleteAll(files);

        // 새로운 review 파일 정보 추가
        saveReviewFiles(req.getImages(), req.getVideo(), review);

        return new ModifyReviewRes();
    }


    /**
     * review 삭제
     */
    @Transactional
    public DeleteReviewRes deleteReview(Long userId, Long feedId) {
        // user가 작성한 리뷰 찾기
        Review review = reviewRepository.findByFeedIdAndUserId(feedId, userId)
                .orElseThrow(() -> new GlobalException(REVIEW_NOT_FOUND));

        // 리뷰에 연결딘 파일 찾기
        List<ReviewFile> files = reviewFileRepository.findAllByReviewId(review.getId());

        // S3에서 파일 데이터 삭제
        s3Service.deleteMultiFiles(files.stream().map(ReviewFile::getUrl).toList(), FilePath.REVIEW);

        // review file 데이터 삭제
        // reviewFileRepository.deleteAllInBatch(files);
        reviewFileRepository.deleteAll(files);

        // review 데이터 삭제
        reviewRepository.delete(review);

        return new DeleteReviewRes(review);
    }

    /**
     * review file 저장
     * - image나 video가 존재하면 저장
     */
    @Transactional
    public void saveReviewFiles(List<MultipartFile> images, MultipartFile video, Review review) {

        List<ReviewFile> files = new ArrayList<>();

        // IMAGE가 존재하는 경우
        if(images != null && !images.isEmpty()){
            addImageFiles(images, review, files);
        }

        // VIDEO가 존재하는 경우
        if(video != null && !video.isEmpty()){
            addVideoFile(video, review, files);
        }

        reviewFileRepository.saveAll(files);
    }

    private void addImageFiles(List<MultipartFile> images, Review review, List<ReviewFile> files) {
        for(MultipartFile image : images){
            files.add(ReviewFile.of(review, s3Service.uploadFile(image, FilePath.REVIEW), FileType.IMAGE));
        }
    }

    private void addVideoFile(MultipartFile video, Review review, List<ReviewFile> files) {
        files.add(ReviewFile.of(review, s3Service.uploadFile(video, FilePath.REVIEW), FileType.VIDEO));
    }
}
