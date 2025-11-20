package com.ssafy.yeobaek.review.controller;

import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import com.ssafy.yeobaek.review.dto.request.CreateReviewRequest;
import com.ssafy.yeobaek.review.dto.response.ReviewListResponse;
import com.ssafy.yeobaek.review.dto.response.ReviewStatistics;
import com.ssafy.yeobaek.review.dto.response.WrittenReviewListResponse;
import com.ssafy.yeobaek.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰", description = "전시 리뷰 관리 API")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "전시 리뷰 작성", description = "전시에 대한 리뷰(평점과 후기)를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "필수 입력값 누락",
                                            value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "평점 범위 오류",
                                            value = "{\"code\": \"RV4001\", \"message\": \"평점은 1~5 사이여야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "후기 길이 오류",
                                            value = "{\"code\": \"RV4002\", \"message\": \"후기는 최대 100자까지 입력 가능합니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 리소스",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 회원",
                                            value = "{\"code\": \"US4041\", \"message\": \"존재하지 않는 회원입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 전시",
                                            value = "{\"code\": \"EX4041\", \"message\": \"존재하지 않는 전시입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "리소스 충돌",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"RV4091\", \"message\": \"이미 작성한 평가입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAnyRole('ARTIST', 'CAFE_OWNER')")
    @PostMapping
    public ResponseEntity<ResponseDto<Void>> createReview(
            @Valid @RequestBody CreateReviewRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        reviewService.createReview(userId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.created());
    }

    @Operation(summary = "내가 받은 평가 통계 조회", description = "현재 로그인한 사용자가 받은 평가의 통계 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"message\": \"정상 처리\", \"data\": {\"totalReviewCount\": 42, \"totalReviewScore\": 4.5, \"ratingStats\": [{\"rating\": 1, \"count\": 2, \"percentage\": 4.8}, {\"rating\": 2, \"count\": 3, \"percentage\": 7.1}, {\"rating\": 3, \"count\": 5, \"percentage\": 11.9}, {\"rating\": 4, \"count\": 10, \"percentage\": 23.8}, {\"rating\": 5, \"count\": 22, \"percentage\": 52.4}]}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAnyRole('ARTIST', 'CAFE_OWNER')")
    @GetMapping("/me/statistics")
    public ResponseEntity<ResponseDto<ReviewStatistics>> getMyReviewStatistics() {
        Long userId = SecurityUtil.getCurrentUserId();
        ReviewStatistics statistics = reviewService.getMyReviewStatistics(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(statistics));
    }

    @Operation(summary = "특정 사용자가 받은 평가 통계 조회", description = "특정 사용자가 받은 평가의 통계 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"message\": \"정상 처리\", \"data\": {\"totalReviewCount\": 42, \"totalReviewScore\": 4.5, \"ratingStats\": [{\"rating\": 1, \"count\": 2, \"percentage\": 4.8}, {\"rating\": 2, \"count\": 3, \"percentage\": 7.1}, {\"rating\": 3, \"count\": 5, \"percentage\": 11.9}, {\"rating\": 4, \"count\": 10, \"percentage\": 23.8}, {\"rating\": 5, \"count\": 22, \"percentage\": 52.4}]}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 리소스",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"US4041\", \"message\": \"존재하지 않는 회원입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @GetMapping("/users/{userId}/statistics")
    public ResponseEntity<ResponseDto<ReviewStatistics>> getUserReviewStatistics(
            @Parameter(description = "조회할 사용자 ID", required = true, example = "1")
            @PathVariable Long userId
    ) {
        ReviewStatistics statistics = reviewService.getUserReviewStatistics(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(statistics));
    }

    @Operation(summary = "내가 받은 리뷰 목록 조회", description = "현재 로그인한 사용자가 받은 리뷰 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"message\": \"정상 처리\", \"data\": {\"reviews\": [{\"reviewId\": 1, \"reviewer\": {\"name\": \"홍길동\", \"profileImageUrl\": \"https://example.com/profile.jpg\", \"role\": \"ARTIST\"}, \"rating\": 5, \"comment\": \"정말 좋은 전시였습니다!\", \"createdAt\": \"2024-01-15\"}], \"pagination\": {\"currentPage\": 0, \"pageSize\": 20, \"totalElements\": 1, \"totalPages\": 1, \"hasNext\": false}}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAnyRole('ARTIST', 'CAFE_OWNER')")
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<ReviewListResponse>> getMyReviews(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 50, message = "INVALID_INPUT_VALUE") int size
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        ReviewListResponse response = reviewService.getMyReviews(userId, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(response));
    }

    @Operation(summary = "특정 사용자가 받은 리뷰 목록 조회", description = "특정 사용자가 받은 리뷰 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"message\": \"정상 처리\", \"data\": {\"reviews\": [{\"reviewId\": 1, \"reviewer\": {\"name\": \"홍길동\", \"profileImageUrl\": \"https://example.com/profile.jpg\", \"role\": \"ARTIST\"}, \"rating\": 5, \"comment\": \"정말 좋은 전시였습니다!\", \"createdAt\": \"2024-01-15\"}], \"pagination\": {\"currentPage\": 0, \"pageSize\": 20, \"totalElements\": 1, \"totalPages\": 1, \"hasNext\": false}}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 리소스",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"US4041\", \"message\": \"존재하지 않는 회원입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseDto<ReviewListResponse>> getUserReviews(
            @Parameter(description = "조회할 사용자 ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 50, message = "INVALID_INPUT_VALUE") int size
    ) {
        ReviewListResponse response = reviewService.getUserReviews(userId, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(response));
    }

    @Operation(summary = "내가 작성한 리뷰 목록 조회", description = "현재 로그인한 사용자가 작성한 리뷰 목록을 페이징하여 조회합니다. rating 파라미터로 특정 평점의 리뷰만 필터링할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"message\": \"정상 처리\", \"data\": {\"reviews\": [{\"reviewId\": 1, \"reviewee\": {\"name\": \"김철수\", \"profileImageUrl\": \"https://example.com/profile.jpg\", \"role\": \"CAFE_OWNER\"}, \"rating\": 5, \"comment\": \"정말 좋은 전시였습니다!\", \"createdAt\": \"2024-01-15\"}], \"pagination\": {\"currentPage\": 0, \"pageSize\": 20, \"totalElements\": 1, \"totalPages\": 1, \"hasNext\": false}}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @PreAuthorize("hasAnyRole('ARTIST', 'CAFE_OWNER')")
    @GetMapping("/me/written")
    public ResponseEntity<ResponseDto<WrittenReviewListResponse>> getMyWrittenReviews(
            @Parameter(description = "필터링할 평점 (1~5, null이면 전체)", example = "5")
            @RequestParam(required = false)
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 5, message = "INVALID_INPUT_VALUE") Integer rating,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 50, message = "INVALID_INPUT_VALUE") int size
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        WrittenReviewListResponse response = reviewService.getMyWrittenReviews(userId, rating, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(response));
    }
}
