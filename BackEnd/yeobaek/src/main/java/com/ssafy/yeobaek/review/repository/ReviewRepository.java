package com.ssafy.yeobaek.review.repository;

import com.ssafy.yeobaek.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 리뷰어와 전시로 리뷰 존재 여부 확인
     * @param reviewerId 리뷰 작성자 ID
     * @param exhibitionId 전시 ID
     * @return 리뷰 존재 여부
     */
    boolean existsByReviewerIdAndExhibitionId(Long reviewerId, Long exhibitionId);

    /**
     * 특정 사용자가 받은 모든 리뷰 조회
     * @param revieweeId 리뷰를 받은 사용자 ID
     * @return 리뷰 목록
     */
    List<Review> findAllByRevieweeId(Long revieweeId);

    /**
     * 특정 사용자가 받은 리뷰 페이징 조회
     * @param revieweeId 리뷰를 받은 사용자 ID
     * @param pageable 페이징 정보
     * @return 리뷰 페이지
     */
    Page<Review> findAllByRevieweeId(Long revieweeId, Pageable pageable);

    /**
     * 특정 사용자가 작성한 리뷰 페이징 조회
     * @param reviewerId 리뷰 작성자 ID
     * @param pageable 페이징 정보
     * @return 리뷰 페이지
     */
    Page<Review> findAllByReviewerId(Long reviewerId, Pageable pageable);

    /**
     * 특정 사용자가 작성한 특정 평점의 리뷰 페이징 조회
     * @param reviewerId 리뷰 작성자 ID
     * @param rating 평점
     * @param pageable 페이징 정보
     * @return 리뷰 페이지
     */
    Page<Review> findAllByReviewerIdAndRating(Long reviewerId, Integer rating, Pageable pageable);

    /**
     * 여러 사용자가 받은 모든 리뷰 일괄 조회 (평점 계산용)
     * @param revieweeIds 리뷰를 받은 사용자 ID 목록
     * @return 리뷰 목록
     */
    List<Review> findAllByRevieweeIdIn(List<Long> revieweeIds);
}
