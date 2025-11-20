package com.ssafy.yeobaek.review.service;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.exhibition.repository.ExhibitionRepository;
import com.ssafy.yeobaek.review.dto.request.CreateReviewRequest;
import com.ssafy.yeobaek.review.dto.response.RatingStat;
import com.ssafy.yeobaek.review.dto.response.ReviewListResponse;
import com.ssafy.yeobaek.review.dto.response.ReviewStatistics;
import com.ssafy.yeobaek.review.dto.response.WrittenReviewListResponse;
import com.ssafy.yeobaek.review.entity.Review;
import com.ssafy.yeobaek.review.mapper.ReviewMapper;
import com.ssafy.yeobaek.review.repository.ReviewRepository;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ExhibitionRepository exhibitionRepository;
    private final ReviewMapper reviewMapper;

    /**
     * 전시 리뷰 작성
     * @param userId 리뷰 작성자 ID
     * @param request 리뷰 작성 요청
     */
    @Override
    @Transactional
    public void createReview(Long userId, CreateReviewRequest request) {
        log.info("리뷰 작성 시작 - 작성자 ID: {}, 전시 ID: {}, 대상자 ID: {}, 평점: {}, 후기: {}",
                userId, request.getExhibitionId(), request.getRevieweeId(), request.getRating(), request.getComment());

        // 기존 리뷰 존재 여부 확인 (중복 체크를 먼저 수행)
        if (reviewRepository.existsByReviewerIdAndExhibitionId(userId, request.getExhibitionId())) {
            log.warn("중복 리뷰 작성 시도 - 작성자 ID: {}, 전시 ID: {}", userId, request.getExhibitionId());
            throw new CustomException(ResponseCode.REVIEW_ALREADY_EXISTS);
        }

        // 리뷰 작성자 조회
        User reviewer = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // 리뷰 대상자 조회
        User reviewee = userService.findById(request.getRevieweeId())
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // TODO: 추후 Exhibition 구현 되면 Service로 의존성 변경, 실제 전시 유저(카페 사장, 작가)인지 검증 필요
        // 전시 조회
        Exhibition exhibition = exhibitionRepository.findById(request.getExhibitionId())
                .orElseThrow(() -> new CustomException(ResponseCode.EXHIBITION_NOT_FOUND));

        // Mapper를 사용해서 Review 엔티티로 변환
        Review review = reviewMapper.toEntity(request, reviewer, reviewee, exhibition);

        // 저장
        reviewRepository.save(review);
        log.info("리뷰 작성 완료 - 리뷰 ID: {}, 작성자: {}, 대상자: {}, 전시 ID: {}",
                review.getId(), reviewer.getNickname(), reviewee.getNickname(), exhibition.getId());
    }

    /**
     * 내가 받은 평가 통계 조회
     * @param userId 사용자 ID
     * @return 평가 통계
     */
    @Override
    public ReviewStatistics getMyReviewStatistics(Long userId) {
        return getReviewStatisticsByUserId(userId);
    }

    /**
     * 특정 사용자가 받은 평가 통계 조회
     * @param userId 조회할 사용자 ID
     * @return 평가 통계
     */
    @Override
    public ReviewStatistics getUserReviewStatistics(Long userId) {
        // 사용자 존재 여부 확인
        userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        return getReviewStatisticsByUserId(userId);
    }

    /**
     * 특정 사용자가 받은 평가 통계 조회 (내부 메소드)
     * @param userId 사용자 ID
     * @return 평가 통계
     */
    private ReviewStatistics getReviewStatisticsByUserId(Long userId) {
        log.info("평가 통계 조회 시작 - 사용자 ID: {}", userId);

        // 사용자가 받은 모든 리뷰 조회
        List<Review> reviews = reviewRepository.findAllByRevieweeId(userId);

        // 통계 계산
        ReviewStatistics statistics = calculateReviewStatistics(reviews);

        log.info("평가 통계 조회 완료 - 사용자 ID: {}, 전체 리뷰 수: {}, 평균 평점: {}",
                userId, statistics.getTotalReviewCount(), statistics.getTotalReviewScore());

        return statistics;
    }

    /**
     * 리뷰 목록으로부터 통계 정보를 계산
     * @param reviews 리뷰 목록
     * @return 평가 통계
     */
    private ReviewStatistics calculateReviewStatistics(List<Review> reviews) {
        // 전체 리뷰 개수
        int totalReviewCount = reviews.size();

        // 평균 평점 계산
        double totalReviewScore = 0.0;
        if (totalReviewCount > 0) {
            totalReviewScore = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
        }

        // 각 별점별 개수 집계
        Map<Integer, Long> ratingCountMap = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

        // 별점별 통계 생성 (1-5점)
        List<RatingStat> ratingStats = IntStream.rangeClosed(1, 5)
                .mapToObj(rating -> {
                    long count = ratingCountMap.getOrDefault(rating, 0L);
                    double percentage = totalReviewCount > 0
                            ? (count * 100.0) / totalReviewCount
                            : 0.0;

                    // Mapper를 통해 DTO 변환
                    return reviewMapper.toRatingStat(
                            rating,
                            (int) count,
                            Math.round(percentage * 10.0) / 10.0
                    );
                })
                .collect(Collectors.toList());

        // Mapper를 통해 최종 DTO 변환
        return reviewMapper.toReviewStatistics(
                totalReviewCount,
                Math.round(totalReviewScore * 10.0) / 10.0,
                ratingStats
        );
    }

    /**
     * 내가 받은 리뷰 목록 조회
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 리뷰 목록 응답
     */
    @Override
    public ReviewListResponse getMyReviews(Long userId, int page, int size) {
        return getReviewsByUserId(userId, page, size);
    }

    /**
     * 특정 사용자가 받은 리뷰 목록 조회
     * @param userId 조회할 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 리뷰 목록 응답
     */
    @Override
    public ReviewListResponse getUserReviews(Long userId, int page, int size) {
        // 사용자 존재 여부 확인
        userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        return getReviewsByUserId(userId, page, size);
    }

    /**
     * 특정 사용자가 받은 리뷰 목록 조회 (내부 메소드)
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 리뷰 목록 응답
     */
    private ReviewListResponse getReviewsByUserId(Long userId, int page, int size) {
        log.info("리뷰 목록 조회 시작 - 사용자 ID: {}, 페이지: {}, 크기: {}", userId, page, size);

        // 페이징 정보 생성 (최신순 정렬)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 사용자가 받은 리뷰 페이징 조회
        Page<Review> reviewPage = reviewRepository.findAllByRevieweeId(userId, pageable);

        // Mapper를 통해 DTO 변환
        ReviewListResponse response = reviewMapper.toReviewListResponse(reviewPage);

        log.info("리뷰 목록 조회 완료 - 사용자 ID: {}, 조회된 리뷰 수: {}, 전체 리뷰 수: {}",
                userId, reviewPage.getNumberOfElements(), reviewPage.getTotalElements());

        return response;
    }

    /**
     * 내가 작성한 리뷰 목록 조회
     * @param userId 사용자 ID
     * @param rating 필터링할 평점 (null이면 전체)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 작성한 리뷰 목록 응답
     */
    @Override
    public WrittenReviewListResponse getMyWrittenReviews(Long userId, Integer rating, int page, int size) {
        log.info("작성한 리뷰 목록 조회 시작 - 사용자 ID: {}, 평점 필터: {}, 페이지: {}, 크기: {}", userId, rating, page, size);

        // 페이징 정보 생성 (최신순 정렬)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // rating 값에 따라 적절한 조회 메서드 호출
        Page<Review> reviewPage;
        if (rating != null) {
            // 특정 평점의 리뷰만 조회
            reviewPage = reviewRepository.findAllByReviewerIdAndRating(userId, rating, pageable);
        } else {
            // 모든 리뷰 조회
            reviewPage = reviewRepository.findAllByReviewerId(userId, pageable);
        }

        // Mapper를 통해 DTO 변환
        WrittenReviewListResponse response = reviewMapper.toWrittenReviewListResponse(reviewPage);

        log.info("작성한 리뷰 목록 조회 완료 - 사용자 ID: {}, 평점 필터: {}, 조회된 리뷰 수: {}, 전체 리뷰 수: {}",
                userId, rating, reviewPage.getNumberOfElements(), reviewPage.getTotalElements());

        return response;
    }
}
