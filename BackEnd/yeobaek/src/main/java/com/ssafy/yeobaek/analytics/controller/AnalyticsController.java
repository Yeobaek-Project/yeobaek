package com.ssafy.yeobaek.analytics.controller;

import com.ssafy.yeobaek.analytics.dto.request.RecordEventRequest;
import com.ssafy.yeobaek.analytics.service.AnalyticsService;
import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 행동 분석 컨트롤러
 */
@Tag(name = "사용자 행동 분석", description = "사용자 행동 데이터 수집 및 분석 API")
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(
            summary = "사용자 행동 이벤트 기록",
            description = "사용자의 행동 이벤트를 기록합니다. Collaborative Filtering을 위한 데이터 수집에 사용됩니다. " +
                    "Android 클라이언트에서 사용자의 카페/작품 조회, 체류 시간, 스크롤 깊이, 채팅 문의 등의 이벤트를 기록합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "생성 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0001\", \"message\": \"생성이 완료되었습니다\"}"
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
                                            name = "필수 필드 누락",
                                            value = "{\"code\": \"S4000\", \"message\": \"필수 입력값이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "잘못된 입력값",
                                            value = "{\"code\": \"S4001\", \"message\": \"잘못된 입력값입니다\"}"
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
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "회원 정보 없음",
                                    value = "{\"code\": \"US4041\", \"message\": \"존재하지 않는 회원입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @PostMapping("/events")
    public ResponseEntity<ResponseDto<Void>> recordEvent(
            @Valid @RequestBody RecordEventRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        analyticsService.recordEvent(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created());
    }
}
