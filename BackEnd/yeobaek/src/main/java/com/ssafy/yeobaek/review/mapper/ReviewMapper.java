package com.ssafy.yeobaek.review.mapper;

import com.ssafy.yeobaek.common.dto.Pagination;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.review.dto.request.CreateReviewRequest;
import com.ssafy.yeobaek.review.dto.response.*;
import com.ssafy.yeobaek.review.entity.Review;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.storage.service.S3Service;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Review 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class ReviewMapper {

    private final S3Service s3Service;

    public ReviewMapper(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * CreateReviewRequest DTO를 Review 엔티티로 변환
     * @param request CreateReviewRequest DTO
     * @param reviewer 리뷰 작성자 (User)
     * @param reviewee 리뷰 대상자 (User)
     * @param exhibition 전시 (Exhibition)
     * @return Review 엔티티
     */
    public Review toEntity(CreateReviewRequest request, User reviewer, User reviewee, Exhibition exhibition) {
        return Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .exhibition(exhibition)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
    }

    /**
     * 통계 데이터를 ReviewStatistics DTO로 변환
     * @param totalReviewCount 전체 리뷰 수
     * @param totalReviewScore 평균 평점
     * @param ratingStats 별점별 통계 리스트
     * @return 평가 통계 DTO
     */
    public ReviewStatistics toReviewStatistics(int totalReviewCount, double totalReviewScore, List<RatingStat> ratingStats) {
        return ReviewStatistics.builder()
                .totalReviewCount(totalReviewCount)
                .totalReviewScore(totalReviewScore)
                .ratingStats(ratingStats)
                .build();
    }

    /**
     * 별점 통계 데이터를 RatingStat DTO로 변환
     * @param rating 별점
     * @param count 개수
     * @param percentage 비율
     * @return 별점 통계 DTO
     */
    public RatingStat toRatingStat(int rating, int count, double percentage) {
        return RatingStat.builder()
                .rating(rating)
                .count(count)
                .percentage(percentage)
                .build();
    }

    /**
     * Review Page를 ReviewListResponse DTO로 변환
     * @param reviewPage 리뷰 페이지
     * @return 리뷰 목록 응답 DTO
     */
    public ReviewListResponse toReviewListResponse(Page<Review> reviewPage) {
        List<ReviewDetail> reviews = reviewPage.getContent().stream()
                .map(this::toReviewDetail)
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .currentPage(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .hasNext(reviewPage.hasNext())
                .build();

        return ReviewListResponse.builder()
                .reviews(reviews)
                .pagination(pagination)
                .build();
    }

    /**
     * Review Entity를 ReviewDetail DTO로 변환
     * @param review 리뷰 엔티티
     * @return 리뷰 상세 DTO
     */
    private ReviewDetail toReviewDetail(Review review) {
        return ReviewDetail.builder()
                .reviewId(review.getId())
                .reviewer(toReviewer(review.getReviewer()))
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt().toLocalDate())
                .build();
    }

    /**
     * User Entity를 Reviewer DTO로 변환
     * @param user 사용자 엔티티
     * @return 평가자 DTO
     */
    private Reviewer toReviewer(User user) {
        return Reviewer.builder()
                .name(user.getNickname())
                .profileImageUrl(s3Service.generatePresignedUrlForDownload(user.getProfileImageUrl()))
                .role(user.getUserType().name())
                .build();
    }

    /**
     * Review Page를 WrittenReviewListResponse DTO로 변환 (작성한 리뷰)
     * @param reviewPage 리뷰 페이지
     * @return 작성한 리뷰 목록 응답 DTO
     */
    public WrittenReviewListResponse toWrittenReviewListResponse(Page<Review> reviewPage) {
        List<WrittenReviewDetail> reviews = reviewPage.getContent().stream()
                .map(this::toWrittenReviewDetail)
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .currentPage(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .hasNext(reviewPage.hasNext())
                .build();

        return WrittenReviewListResponse.builder()
                .reviews(reviews)
                .pagination(pagination)
                .build();
    }

    /**
     * Review Entity를 WrittenReviewDetail DTO로 변환 (작성한 리뷰)
     * @param review 리뷰 엔티티
     * @return 작성한 리뷰 상세 DTO
     */
    private WrittenReviewDetail toWrittenReviewDetail(Review review) {
        return WrittenReviewDetail.builder()
                .reviewId(review.getId())
                .reviewee(toReviewee(review.getReviewee()))
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt().toLocalDate())
                .build();
    }

    /**
     * User Entity를 Reviewee DTO로 변환
     * @param user 사용자 엔티티
     * @return 평가 대상자 DTO
     */
    private Reviewee toReviewee(User user) {
        return Reviewee.builder()
                .name(user.getNickname())
                .profileImageUrl(s3Service.generatePresignedUrlForDownload(user.getProfileImageUrl()))
                .role(user.getUserType().name())
                .build();
    }
}
