package com.ureca.juksoon.domain.review.service;

import com.ureca.juksoon.domain.review.dto.ReviewWithFiles;
import com.ureca.juksoon.domain.review.dto.request.ReviewReq;
import com.ureca.juksoon.domain.review.dto.response.CreateReviewRes;
import com.ureca.juksoon.domain.review.dto.response.GetReviewsRes;
import com.ureca.juksoon.domain.review.entity.FileType;
import com.ureca.juksoon.domain.review.entity.Review;
import com.ureca.juksoon.domain.review.entity.ReviewFile;
import com.ureca.juksoon.domain.review.repository.ReviewFileRepository;
import com.ureca.juksoon.domain.review.repository.ReviewRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.s3.FilePath;
import com.ureca.juksoon.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // 이미지 & 비디오: s3 업로드 및 DB 저장
        saveReviewFiles(req.getImages(), req.getVideo(), saveReview);

        return new CreateReviewRes(saveReview.getId());
    }


    /**
     * review 가져오기
     * - Role에 따라 하나 or 다수
     * - review와 reviewfile을 가져와 하나의 dto로 반환
     */
    public GetReviewsRes getReviews(Long userId, UserRole userRole, Long feedId) {

        // 리뷰에 해당하는 파일들 모두 조회
        List<Review> reviews = null;

        if(userRole.equals(UserRole.ROLE_OWNER)){
            reviews = reviewRepository.findAllByFeedId(feedId);

        }

        if(userRole.equals(UserRole.ROLE_TESTER)){
            reviews = reviewRepository.findByFeedIdAndUserId(feedId, userId)
                    .map(List::of)
                    .orElse(List.of());
        }
        
        List<Long> reviewIds = reviews.stream()
                .map(Review::getId)
                .toList();

        // 리뷰에 연결된 파일 전부 조회
        List<ReviewFile> reviewFiles = reviewFileRepository.findAllByReviewIds(reviewIds);

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
     */
//    @Transactional
//    public updateReviewRes updateReview(Long userId, Long feedId, ReviewReq request) {
//    }


    /**
     * review 삭제
     */
//    @Transactional
//    public



    /**
     * review file 저장
     * - image나 video가 존재하면 저장
     */
    @Transactional
    private void saveReviewFiles(List<MultipartFile> images, MultipartFile video, Review review) {

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
