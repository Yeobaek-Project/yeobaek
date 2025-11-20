package com.ssafy.yeobaek.review.service;

import com.ssafy.yeobaek.review.dto.request.CreateReviewRequest;
import com.ssafy.yeobaek.review.dto.response.ReviewListResponse;
import com.ssafy.yeobaek.review.dto.response.ReviewStatistics;
import com.ssafy.yeobaek.review.dto.response.WrittenReviewListResponse;

public interface ReviewService {

    /**
     * 전시 리뷰 작성
     * @param userId 리뷰 작성자 ID
     * @param request 리뷰 작성 요청
     */
    void createReview(Long userId, CreateReviewRequest request);

    /**
     * 내가 받은 평가 통계 조회
     * @param userId 사용자 ID
     * @return 평가 통계
     */
    ReviewStatistics getMyReviewStatistics(Long userId);

    /**
     * 특정 사용자가 받은 평가 통계 조회
     * @param userId 조회할 사용자 ID
     * @return 평가 통계
     */
    ReviewStatistics getUserReviewStatistics(Long userId);

    /**
     * 내가 받은 리뷰 목록 조회
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 리뷰 목록 응답
     */
    ReviewListResponse getMyReviews(Long userId, int page, int size);

    /**
     * 특정 사용자가 받은 리뷰 목록 조회
     * @param userId 조회할 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 리뷰 목록 응답
     */
    ReviewListResponse getUserReviews(Long userId, int page, int size);

    /**
     * 내가 작성한 리뷰 목록 조회
     * @param userId 사용자 ID
     * @param rating 필터링할 평점 (null이면 전체)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 작성한 리뷰 목록 응답
     */
    WrittenReviewListResponse getMyWrittenReviews(Long userId, Integer rating, int page, int size);
}
