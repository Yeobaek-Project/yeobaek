package com.ssafy.yeobaek.exhibition.controller;

import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import com.ssafy.yeobaek.exhibition.dto.request.CreateExhibitionRequest;
import com.ssafy.yeobaek.exhibition.dto.response.ExhibitionSummaryResponse;
import com.ssafy.yeobaek.exhibition.service.ExhibitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "전시 관리", description = "전시 관리 API")
@RestController
@RequestMapping("/api/v1/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @Operation(
            summary = "전시 제안 생성",
            description = "카페 사장 또는 작가가 전시를 제안합니다. 제안자는 자동으로 동의 상태가 되며, 상대방이 동의하면 전시가 확정됩니다.\n\n" +
                    "**카페 사장이 제안**: 특정 작품을 자신의 카페에 전시하고 싶을 때\n" +
                    "**작가가 제안**: 자신의 작품을 특정 카페에 전시하고 싶을 때"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "전시 제안 생성 성공",
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
                                            name = "필수 값 누락",
                                            value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "수수료율 범위 오류",
                                            value = "{\"code\": \"EX4001\", \"message\": \"수수료율은 0~15 사이여야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "추가 조건 길이 초과",
                                            value = "{\"code\": \"EX4002\", \"message\": \"추가 조건은 최대 500자까지 입력 가능합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "전시 기간 오류",
                                            value = "{\"code\": \"EX4003\", \"message\": \"전시 종료일은 시작일보다 늦어야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "과거 날짜",
                                            value = "{\"code\": \"EX4004\", \"message\": \"전시 시작일은 오늘 이후여야 합니다\"}"
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
                            examples = {
                                    @ExampleObject(
                                            name = "카페 권한 없음",
                                            description = "본인의 카페가 아닌 경우",
                                            value = "{\"code\": \"CF4032\", \"message\": \"본인의 카페만 수정/삭제할 수 있습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품 권한 없음",
                                            description = "본인의 작품이 아닌 경우",
                                            value = "{\"code\": \"AW4032\", \"message\": \"본인의 작품만 수정/삭제할 수 있습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "권한 없음",
                                            description = "카페 사장 또는 작가가 아닌 경우",
                                            value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "카페를 찾을 수 없음",
                                            value = "{\"code\": \"CF4041\", \"message\": \"존재하지 않는 카페입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품을 찾을 수 없음",
                                            value = "{\"code\": \"AW4041\", \"message\": \"존재하지 않는 작품입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "슬롯을 찾을 수 없음",
                                            value = "{\"code\": \"CF4042\", \"message\": \"존재하지 않는 슬롯입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "슬롯이 카페에 속하지 않음",
                                            value = "{\"code\": \"CF4044\", \"message\": \"해당 카페에 존재하지 않는 슬롯입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복 예약",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "슬롯 중복 예약",
                                            value = "{\"code\": \"EX4091\", \"message\": \"해당 기간에 이미 예약된 슬롯입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품 중복 예약",
                                            value = "{\"code\": \"EX4092\", \"message\": \"해당 기간에 이미 다른 전시가 예정된 작품입니다\"}"
                                    )
                            }
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
    @PostMapping
    @PreAuthorize("hasAnyRole('CAFE_OWNER', 'ARTIST')")
    public ResponseEntity<ResponseDto<Void>> createExhibitionProposal(
            @Valid @RequestBody CreateExhibitionRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        exhibitionService.createExhibitionProposal(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created());
    }

    @Operation(
            summary = "전시 제안 동의",
            description = "제안받은 전시에 동의합니다. 카페 사장과 작가 모두 동의하면 전시가 확정됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "전시 제안 동의 성공",
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
                                            name = "대기 중이 아닌 전시",
                                            value = "{\"code\": \"EX4005\", \"message\": \"대기 중인 전시 제안만 동의/거절할 수 있습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "이미 동의한 전시",
                                            value = "{\"code\": \"EX4006\", \"message\": \"이미 동의한 전시 제안입니다\"}"
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
                            examples = {
                                    @ExampleObject(
                                            name = "전시 권한 없음",
                                            description = "해당 전시의 카페 사장 또는 작가가 아닌 경우",
                                            value = "{\"code\": \"EX4031\", \"message\": \"해당 전시에 대한 권한이 없습니다\"}"
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
                                    name = "전시를 찾을 수 없음",
                                    value = "{\"code\": \"EX4041\", \"message\": \"존재하지 않는 전시입니다\"}"
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
    @PostMapping("/{exhibitionId}/agree")
    @PreAuthorize("hasAnyRole('CAFE_OWNER', 'ARTIST')")
    public ResponseEntity<ResponseDto<Void>> agreeExhibition(
            @PathVariable Long exhibitionId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        exhibitionService.agreeExhibition(userId, exhibitionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }

    @Operation(
            summary = "전시 제안 거절",
            description = "제안받은 전시를 거절합니다. 거절하면 전시가 REJECTED 상태로 변경됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "전시 제안 거절 성공",
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
                            examples = @ExampleObject(
                                    name = "대기 중이 아닌 전시",
                                    value = "{\"code\": \"EX4005\", \"message\": \"대기 중인 전시 제안만 동의/거절할 수 있습니다\"}"
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
                                    name = "전시 권한 없음",
                                    description = "해당 전시의 카페 사장 또는 작가가 아닌 경우",
                                    value = "{\"code\": \"EX4031\", \"message\": \"해당 전시에 대한 권한이 없습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "전시를 찾을 수 없음",
                                    value = "{\"code\": \"EX4041\", \"message\": \"존재하지 않는 전시입니다\"}"
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
    @PostMapping("/{exhibitionId}/reject")
    @PreAuthorize("hasAnyRole('CAFE_OWNER', 'ARTIST')")
    public ResponseEntity<ResponseDto<Void>> rejectExhibition(
            @PathVariable Long exhibitionId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        exhibitionService.rejectExhibition(userId, exhibitionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }

    @Operation(
            summary = "전시 일정 정보 조회",
            description = "전시 ID로 전시의 상세 정보를 조회합니다. 카페, 작품, 슬롯의 간략한 정보를 포함합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "전시 일정 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": {\"exhibitionId\": 1, \"cafe\": {\"cafeId\": 1, \"cafeName\": \"여백 갤러리\", \"address\": \"서울특별시 강남구 테헤란로 123\"}, \"artwork\": {\"artworkId\": 1, \"title\": \"여름의 여백\", \"widthCm\": 120.5, \"heightCm\": 80.0, \"weightKg\": 5.2, \"artistName\": \"김작가\"}, \"startDate\": \"2025-11-15\", \"endDate\": \"2025-12-15\", \"commissionRate\": 10.0, \"requirements\": \"작품 조명은 자연광으로 부탁드립니다.\"}, \"message\": \"정상 처리\"}"
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
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "전시를 찾을 수 없음",
                                    value = "{\"code\": \"EX4041\", \"message\": \"존재하지 않는 전시입니다\"}"
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
    @GetMapping("/{exhibitionId}")
    public ResponseEntity<ResponseDto<ExhibitionSummaryResponse>> getExhibitionDetail(
            @Parameter(description = "조회할 전시 ID", required = true)
            @PathVariable Long exhibitionId
    ) {
        ExhibitionSummaryResponse data = exhibitionService.getExhibitionSummary(exhibitionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(
            summary = "슬롯별 전시 일정 조회",
            description = "특정 슬롯의 특정 날짜 이후 전시 일정 목록을 조회합니다.\n\n" +
                    "**조회 조건**:\n" +
                    "- 전시 종료일이 조회 기준 날짜(from) 이후인 전시\n" +
                    "- APPROVED 또는 ONGOING 상태의 전시만\n" +
                    "- 전시 시작일 기준 오름차순 정렬"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "슬롯별 전시 일정 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": [{\"exhibitionId\": 1, \"artwork\": {\"artworkId\": 1, \"title\": \"여름의 여백\", \"description\": \"여름 바다의 고요한 여백을 담은 작품\", \"artCategory\": \"LANDSCAPE\"}, \"startDate\": \"2025-11-15\", \"endDate\": \"2025-12-15\", \"exhibitionState\": \"ONGOING\"}, {\"exhibitionId\": 2, \"artwork\": {\"artworkId\": 2, \"title\": \"가을의 초상\", \"description\": \"가을의 분위기를 담은 인물화\", \"artCategory\": \"PORTRAIT\"}, \"startDate\": \"2025-12-20\", \"endDate\": \"2026-01-20\", \"exhibitionState\": \"COMPLETED\"}], \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "필수 파라미터 누락",
                                    value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "슬롯을 찾을 수 없음",
                                    value = "{\"code\": \"CF4042\", \"message\": \"존재하지 않는 슬롯입니다\"}"
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
    @GetMapping("/slots/{slotId}")
    public ResponseEntity<ResponseDto<List<ExhibitionSummaryResponse>>> getExhibitionsBySlot(
            @Parameter(description = "조회할 슬롯 ID", required = true)
            @PathVariable Long slotId,
            @Parameter(description = "조회 기준 날짜", required = true, example = "2025-11-10")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from
    ) {
        List<ExhibitionSummaryResponse> data = exhibitionService.getExhibitionsBySlot(slotId, from);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }
}
