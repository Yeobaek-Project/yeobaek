package com.ssafy.yeobaek.cafe.controller;

import com.ssafy.yeobaek.cafe.service.CafeBookmarkService;
import com.ssafy.yeobaek.common.dto.Pagination;
import com.ssafy.yeobaek.cafe.dto.request.*;
import com.ssafy.yeobaek.cafe.dto.response.*;
import com.ssafy.yeobaek.cafe.service.CafeCategoryService;
import com.ssafy.yeobaek.cafe.service.CafeService;
import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.util.SecurityUtil;
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
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Tag(name = "카페 관리", description = "카페 관리 API")
@RestController
@RequestMapping("/api/v1/cafes")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;
    private final CafeCategoryService cafeCategoryService;
    private final CafeBookmarkService cafeBookmarkService;

    @Operation(summary = "카페 정보 등록", description = "카페 사장이 매장 상세 정보, 전시 슬롯, 전시 조건을 등록합니다. 회원가입 후 처음 카페 정보를 등록할 때 사용합니다. 이미지는 최소 3장, 최대 10장까지 등록 가능합니다.")
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
                                    @ExampleObject(name = "이미지 개수 오류", value = "{\"code\": \"CF4001\", \"message\": \"이미지는 3~10장 등록해야 합니다\"}"),
                                    @ExampleObject(name = "매장명 길이 오류", value = "{\"code\": \"CF4004\", \"message\": \"매장명은 2~15자 이내여야 합니다\"}"),
                                    @ExampleObject(name = "카페 소개 길이 오류", value = "{\"code\": \"CF4005\", \"message\": \"카페 소개는 최대 500자까지 입력 가능합니다\"}"),
                                    @ExampleObject(name = "영업시간 형식 오류", value = "{\"code\": \"CF4007\", \"message\": \"영업시간 형식이 올바르지 않습니다 (HH:mm)\"}"),
                                    @ExampleObject(name = "유효하지 않은 주소 오류", value = "{\"code\": \"CF4009\", \"message\": \"좌표로 변환할 수 없는 주소입니다\"}"),
                                    @ExampleObject(name = "필수 값 누락", value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
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
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4043\", \"message\": \"존재하지 않는 카테고리입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복 등록",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4091\", \"message\": \"이미 등록된 카페가 있습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"),
                                    @ExampleObject(value = "{\"code\": \"S5001\", \"message\": \"지오코더 API 서버에 오류가 발생했습니다\"}")
                            }
                    )
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('CAFE_OWNER')")
    public ResponseEntity<ResponseDto<Void>> createCafe(@Valid @RequestBody CreateCafeRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        cafeService.createCafe(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created());
    }

    @Operation(summary = "카페 정보 수정", description = "등록된 내 카페 정보를 수정합니다. 모든 필드는 선택적으로 수정 가능합니다 (전송된 필드만 수정됨). 슬롯 정보는 별도 API로 관리합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
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
                                    @ExampleObject(name = "이미지 개수 오류", value = "{\"code\": \"CF4001\", \"message\": \"이미지는 3~10장 등록해야 합니다\"}"),
                                    @ExampleObject(name = "매장명 길이 오류", value = "{\"code\": \"CF4004\", \"message\": \"매장명은 2~15자 이내여야 합니다\"}"),
                                    @ExampleObject(name = "카페 소개 길이 오류", value = "{\"code\": \"CF4005\", \"message\": \"카페 소개는 최대 500자까지 입력 가능합니다\"}"),
                                    @ExampleObject(name = "영업시간 형식 오류", value = "{\"code\": \"CF4007\", \"message\": \"영업시간 형식이 올바르지 않습니다 (HH:mm)\"}"),
                                    @ExampleObject(name = "유효하지 않은 주소 오류", value = "{\"code\": \"CF4009\", \"message\": \"좌표로 변환할 수 없는 주소입니다\"}"),
                                    @ExampleObject(name = "필수 값 누락", value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"만료된 토큰입니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"),
                                    @ExampleObject(value = "{\"code\": \"CF4032\", \"message\": \"본인의 카페만 수정/삭제할 수 있습니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{\"code\": \"CF4041\", \"message\": \"존재하지 않는 카페입니다\"}"),
                                    @ExampleObject(value = "{\"code\": \"CF4043\", \"message\": \"존재하지 않는 카테고리입니다\"}")
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
    @PatchMapping("/me")
    @PreAuthorize("hasRole('CAFE_OWNER')")
    public ResponseEntity<ResponseDto<Void>> updateCafe(
            @Valid @RequestBody UpdateCafeRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        cafeService.updateCafe(userId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }

    @Operation(summary = "카페 공간 등록", description = "카페 사장이 카페 내부 공간을 등록합니다. 카페 등록 후 처음 카페 공간을 등록할 때 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\":\"S0000\",\"data\":[{\"wallId\":5,\"wallName\":\"벽 A\",\"start\":{\"x\":1.2,\"y\":3.4},\"end\":{\"x\":1.2,\"y\":3.4},\"isDisplayable\":true}],\"message\":\"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "벽 이름 길이 오류", value = "{\"code\": \"CF4002\", \"message\": \"카페 벽 이름은 2~15자 이내여야 합니다\"}"),
                                    @ExampleObject(name = "필수 값 누락", value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
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
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4041\", \"message\": \"존재하지 않는 카페입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복 등록",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4094\", \"message\": \"이미 등록된 카페 공간이 있습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                                    )
                            }
                    )
            )
    })
    @PostMapping("/walls")
    @PreAuthorize("hasRole('CAFE_OWNER')")
    public ResponseEntity<ResponseDto<List<Wall>>> createCafeWalls(@Valid @RequestBody List<CreateDisplayWallRequest> request) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Wall> createdWalls = cafeService.createCafeWall(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created(createdWalls));
    }

    @Operation(summary = "내 카페 정보 조회", description = "현재 로그인한 카페 사장의 카페 정보를 조회합니다. 수정 목적으로 사용되며, 전체 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{\"code\":\"S0000\",\"data\":{\"myCafe\":{\"cafeId\":1,\"cafeName\":\"카페 여백\",\"address\":\"경기도 안양시 동안구 관양동 1588-8\",\"primaryImageUrl\":\"https://example.com/cafe1.jpg\",\"totalSlots\":3,\"exhibitingSlots\":1,\"collaboratingArtists\":15,\"displaySlots\":[{\"wallId\":1,\"wallName\":\"벽 A\",\"start\":{\"x\":1.2,\"y\":3.4},\"end\":{\"x\":1.2,\"y\":3.4},\"width\":120,\"height\":100,\"isDisplayable\":true,\"isExhibiting\":false,\"commissionRate\":0},{\"wallId\":2,\"wallName\":\"벽 B\",\"start\":{\"x\":1.2,\"y\":3.4},\"end\":{\"x\":1.2,\"y\":3.4},\"width\":0,\"height\":100,\"isDisplayable\":false},{\"wallId\":3,\"wallName\":\"벽 C\",\"start\":{\"x\":87.4,\"y\":67.4},\"end\":{\"x\":45.7,\"y\":45.7},\"width\":208.3,\"height\":100.5,\"isDisplayable\":true,\"isExhibiting\":true,\"commissionRate\":10,\"requirements\":\"잘 부탁드립니다\",\"currentExhibition\":{\"artworkId\":1,\"artworkTitle\":\"여름의 여백\",\"artworkImageUrl\":\"https://example.com/artwork1.jpg\",\"artistName\":\"홍길동\",\"exhibitionStartDate\":\"2024-01-01\",\"exhibitionEndDate\":\"2024-03-31\"}}]}},\"message\":\"정상 처리\"}\n"
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
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4031\", \"message\": \"카페 사장만 조회할 수 있습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4041\", \"message\": \"존재하지 않는 카페입니다\"}"
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
    @PreAuthorize("hasRole('CAFE_OWNER')")
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<MyCafeData>> getMyCafe() {
        Long userId = SecurityUtil.getCurrentUserId();
        MyCafeData data = cafeService.getMyCafe(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "슬롯 정보 수정", description = "등록된 슬롯 정보를 수정합니다. 모든 필드는 선택적으로 수정 가능합니다 (전송된 필드만 수정됨). 카페 정보는 별도 API로 관리합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\":\"S0000\",\"data\":{\"slot\":{\"wallId\":5,\"wallName\":\"벽 A\",\"width\":200,\"height\":150,\"status\":\"AVAILABLE\",\"commissionRate\":10,\"requirements\":\"홍보용 작가 소개 카드 또는 작품 설명 자료를 함께 제공해주세요.\"}},\"message\":\"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "요청사항 길이 오류", value = "{\"code\": \"CF4003\", \"message\": \"기타 요청사항은 500자 이내여야 합니다\"}"),
                                    @ExampleObject(name = "수수료율 범위 오류", value = "{\"code\": \"CF4006\", \"message\": \"수수료율은 0~15 사이여야 합니다\"}"),
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "일반 권한 없음", value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"),
                                    @ExampleObject(name = "리소스 권한 없음", value = "{\"code\": \"CF4032\", \"message\": \"본인의 카페만 수정/삭제할 수 있습니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "존재하지 않는 슬롯", value = "{\"code\": \"CF4042\", \"message\": \"존재하지 않는 슬롯입니다\"}"),
                                    @ExampleObject(name = "카페 내 슬롯 없음", value = "{\"code\": \"CF4044\", \"message\": \"해당 카페에 존재하지 않는 슬롯입니다\"}")
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
    @PreAuthorize("hasRole('CAFE_OWNER')")
    @PatchMapping("/{cafeId}/slots/{slotId}")
    public ResponseEntity<ResponseDto<SlotData>> updateSlot(
            @Parameter(description = "수정할 카페 ID", required = true)
            @PathVariable Long cafeId,
            @Parameter(description = "수정할 슬롯 ID", required = true)
            @PathVariable Long slotId,
            @Valid @RequestBody UpdateSlotRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        SlotData slot = cafeService.updateSlot(userId, cafeId, slotId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(slot));
    }

    @Operation(summary = "카페 북마크 목록 조회", description = "현재 사용자가 북마크한 카페 목록을 조회합니다. 최신 북마크 순으로 정렬됩니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\":\"S0000\",\"message\":\"정상 처리\",\"data\":{\"cafes\":[{\"cafeId\":1,\"cafeName\":\"카페 여백 #1\",\"address\":\"서울시 강남구 테헤란로 123\",\"latitude\":37.5665,\"longitude\":126.978,\"primaryImageUrl\":\"https://example.com/cafe1.jpg\",\"availableSlotCount\":2,\"totalSlotCount\":3,\"distance\":null,\"categories\":[\"모던\",\"미니멀\"]},{\"cafeId\":2,\"cafeName\":\"아트 카페 #2\",\"address\":\"서울시 종로구 인사동길 45\",\"latitude\":37.5796,\"longitude\":126.9769,\"primaryImageUrl\":\"https://example.com/cafe2.jpg\",\"availableSlotCount\":3,\"totalSlotCount\":4,\"distance\":null,\"categories\":[\"빈티지\",\"클래식\"]}],\"pagination\":{\"page\":0,\"size\":20,\"totalElements\":100,\"totalPages\":5,\"hasNext\":false}}}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
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
    @GetMapping("/bookmarks")
    public ResponseEntity<ResponseDto<CafeListData>> getBookmarkedCafes(
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        CafeListData data = cafeService.getBookmarkedCafes(userId, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "인기 카페 목록 조회", description = "조회수, 북마크 수를 기반으로 인기 카페를 추천합니다. 최대 20개까지 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\":\"S0000\",\"message\":\"정상 처리\",\"data\":{\"cafes\":[{\"cafeId\":1,\"cafeName\":\"카페 여백 #1\",\"address\":\"서울시 강남구 테헤란로 123\",\"latitude\":37.5665,\"longitude\":126.978,\"primaryImageUrl\":\"https://example.com/cafe1.jpg\",\"availableSlotCount\":2,\"totalSlotCount\":3,\"distance\":null,\"categories\":[\"모던\",\"미니멀\"]},{\"cafeId\":2,\"cafeName\":\"아트 카페 #2\",\"address\":\"서울시 종로구 인사동길 45\",\"latitude\":37.5796,\"longitude\":126.9769,\"primaryImageUrl\":\"https://example.com/cafe2.jpg\",\"availableSlotCount\":3,\"totalSlotCount\":4,\"distance\":null,\"categories\":[\"빈티지\",\"클래식\"]}]}}"
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
                                            name = "조회 개수 오류",
                                            description = "조회할 카페 수가 1~20개를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
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
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
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
    @GetMapping("/popular")
    public ResponseEntity<ResponseDto<PopularCafesData>> getPopularCafes(
            @Parameter(description = "조회할 카페 수 (기본값: 10, 최대: 20)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 20, message = "INVALID_INPUT_VALUE")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PopularCafesData data = cafeService.getPopularCafes(size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "카페 목록 조회", description = "카페 목록을 조회하거나 검색합니다. 키워드 검색, 필터링을 제공합니다. 역할별로 사용 가능한 필터가 다릅니다. 정렬은 무조건 거리순입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\":\"S0000\",\"message\":\"정상 처리\",\"data\":{\"cafes\":[{\"cafeId\":1,\"cafeName\":\"카페 여백 #1\",\"address\":\"서울시 강남구 테헤란로 123\",\"latitude\":37.5665,\"longitude\":126.978,\"primaryImageUrl\":\"https://example.com/cafe1.jpg\",\"availableSlotCount\":2,\"totalSlotCount\":3,\"distance\":0.5,\"categories\":[\"모던\",\"미니멀\"]},{\"cafeId\":2,\"cafeName\":\"아트 카페 #2\",\"address\":\"서울시 종로구 인사동길 45\",\"latitude\":37.5796,\"longitude\":126.9769,\"primaryImageUrl\":\"https://example.com/cafe2.jpg\",\"availableSlotCount\":3,\"totalSlotCount\":4,\"distance\":1.2,\"categories\":[\"빈티지\",\"클래식\"]}],\"pagination\":{\"page\":0,\"size\":20,\"totalElements\":100,\"totalPages\":5,\"hasNext\":false}}}"
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
                                            name = "키워드 길이 초과",
                                            description = "keyword가 100자를 초과한 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "위도 범위 오류",
                                            description = "latitude가 -90~90 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "경도 범위 오류",
                                            description = "longitude가 -180~180 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "너비 크기 범위 오류",
                                            description = "minWidth 또는 maxWidth가 1-500 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "높이 크기 범위 오류",
                                            description = "minHeight 또는 maxHeight가 1-500 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "수수료율 범위 오류",
                                            description = "minCommission 또는 maxCommission이 0-15 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "페이지 번호 오류",
                                            description = "page가 0 미만인 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "페이지 크기 오류",
                                            description = "size가 1-50 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
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
                                    @ExampleObject(name = "슬롯 규격 필터 권한 없음", value = "{\"code\": \"CF4033\", \"message\": \"슬롯 규격 필터는 작가만 사용할 수 있습니다\"}"),
                                    @ExampleObject(name = "수수료율 필터 권한 없음", value = "{\"code\": \"CF4034\", \"message\": \"수수료율 필터는 작가만 사용할 수 있습니다\"}"),
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
    @GetMapping
    public ResponseEntity<ResponseDto<CafeListData>> searchCafes(
            @Parameter(description = "카페명 검색 키워드 (최대 100자)")
            @Size(max = 100, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "사용자 현재 위치 위도", required = true)
            @Min(value = -90, message = "INVALID_INPUT_VALUE")
            @Max(value = 90, message = "INVALID_INPUT_VALUE")
            @RequestParam Double myLatitude,

            @Parameter(description = "사용자 현재 위치 경도", required = true)
            @Min(value = -180, message = "INVALID_INPUT_VALUE")
            @Max(value = 180, message = "INVALID_INPUT_VALUE")
            @RequestParam Double myLongitude,

            @Parameter(description = "지도 중심 위도", required = true)
            @Min(value = -90, message = "INVALID_INPUT_VALUE")
            @Max(value = 90, message = "INVALID_INPUT_VALUE")
            @RequestParam Double centerLatitude,

            @Parameter(description = "지도 중심 경도", required = true)
            @Min(value = -180, message = "INVALID_INPUT_VALUE")
            @Max(value = 180, message = "INVALID_INPUT_VALUE")
            @RequestParam Double centerLongitude,

            @Parameter(description = "조회 반경 (km)", required = true)
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 100, message = "INVALID_INPUT_VALUE")
            @RequestParam Double radiusKm,

            @Parameter(description = "슬롯 최소 폭 (cm)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 500, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Integer minWidth,

            @Parameter(description = "슬롯 최대 폭 (cm)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 500, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Integer maxWidth,

            @Parameter(description = "슬롯 최소 높이 (cm)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 500, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Integer minHeight,

            @Parameter(description = "슬롯 최대 높이 (cm)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 500, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Integer maxHeight,

            @Parameter(description = "최소 수수료율 (%)")
            @Min(value = 0, message = "INVALID_INPUT_VALUE")
            @Max(value = 30, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Double minCommission,

            @Parameter(description = "최대 수수료율 (%)")
            @Min(value = 0, message = "INVALID_INPUT_VALUE")
            @Max(value = 15, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Double maxCommission,

            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @Min(value = 0, message = "INVALID_INPUT_VALUE")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 50, message = "INVALID_INPUT_VALUE")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Boolean isArtist = SecurityUtil.getCurrentUserType().equals(UserRole.ARTIST);

        CafeListData data = cafeService.searchCafes(isArtist, keyword, myLatitude, myLongitude, centerLatitude, centerLongitude, radiusKm, minWidth, maxWidth, minHeight, maxHeight, minCommission, maxCommission, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "추천 카페 목록 조회", description = "특정 작품에 어울리는 카페 목록을 추천합니다. 현재는 임시로 전체 카페를 ID 오름차순으로 반환하며, 추후 추천 시스템 연결 예정입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "추천 카페가 있는 경우",
                                            description = "추천할 카페가 있는 경우 (전체 카페 페이징)",
                                            value = "{\"code\": \"S0000\", \"data\": {\"cafes\": [{\"cafeId\": 1, \"cafeName\": \"카페 여백\", \"address\": \"서울시 강남구 테헤란로 123\", \"primaryImageUrl\": \"https://s3.presigned.url/cafe1.jpg\", \"categories\": [\"모던\", \"미니멀\"], \"totalReviewScore\": 4.5, \"slots\": [{\"wallName\": \"입구 벽\", \"isExhibiting\": false}, {\"wallName\": \"중앙 벽\", \"isExhibiting\": true}]}, {\"cafeId\": 2, \"cafeName\": \"갤러리 카페\", \"address\": \"서울시 종로구 인사동 45\", \"primaryImageUrl\": \"https://s3.presigned.url/cafe2.jpg\", \"categories\": [\"빈티지\"], \"totalReviewScore\": 4.2, \"slots\": [{\"wallName\": \"메인 벽\", \"isExhibiting\": false}]}], \"pagination\": {\"currentPage\": 0, \"pageSize\": 20, \"totalElements\": 45, \"totalPages\": 3, \"hasNext\": true}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "추천 카페가 없는 경우",
                                            description = "추천할 카페가 없는 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"cafes\": [], \"pagination\": {\"currentPage\": 0, \"pageSize\": 20, \"totalElements\": 0, \"totalPages\": 0, \"hasNext\": false}}, \"message\": \"정상 처리\"}"
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
                                    value = "{\"code\": \"AW4041\", \"data\": null, \"message\": \"존재하지 않는 작품입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @GetMapping("/recommendations")
    public ResponseEntity<ResponseDto<CafeRecommendationResponse>> getRecommendedCafes(
            @Parameter(description = "작품 ID", required = true)
            @RequestParam Long artworkId,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // TODO: 임시 구현: 전체 카페 페이징 조회 (추후 추천 시스템 연결 예정)
        CafeRecommendationResponse data = cafeService.getRecommendedCafes(artworkId, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "카페 상세 조회", description = "특정 카페의 상세 정보를 조회합니다. 기본 정보, 이미지, 전시 슬롯, 슬롯별 전시 상태를 포함합니다. 조회수가 증가합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\":\"S0000\",\"data\":{\"cafeDetail\":{\"cafeId\":1,\"cafeName\":\"카페 여백\",\"address\":\"경기도 안양시 동안구 관양동 1588-8\",\"description\":\"조용하고 아늑한 분위기의 카페입니다.\",\"contact\":\"02-1234-5678\",\"businessHoursStart\":\"09:00\",\"businessHoursEnd\":\"22:00\",\"dailyVisitors\":\"100~200명\",\"viewCount\":9,\"isBookmarked\":false,\"categories\":[\"모던\",\"빈티지\"],\"images\":[\"https://example.com/cafe1.jpg\",\"https://example.com/cafe2.jpg\",\"https://example.com/cafe3.jpg\"],\"displaySlots\":[{\"wallId\":1,\"wallName\":\"벽 A\",\"start\":{\"x\":1.2,\"y\":3.4},\"end\":{\"x\":1.2,\"y\":3.4},\"width\":120,\"height\":100,\"isDisplayable\": true,\"isExhibiting\": false,\"commissionRate\":0},{\"wallId\":2,\"wallName\":\"벽 B\",\"start\":{\"x\":1.2,\"y\":3.4},\"end\":{\"x\":1.2,\"y\":3.4},\"width\":0,\"height\":100,\"isDisplayable\":false},{\"wallId\":3,\"wallName\":\"벽 C\",\"start\":{\"x\":87.4,\"y\":67.4},\"end\":{\"x\":45.7,\"y\":45.7},\"width\":208.3,\"height\":100.5,\"isDisplayable\":true,\"isExhibiting\":true,\"commissionRate\":10,\"requirements\":\"잘 부탁드립니다\",\"currentExhibition\":{\"artworkId\":1,\"artworkTitle\":\"여름의 여백\",\"artworkImageUrl\":\"https://example.com/artwork1.jpg\",\"artistName\":\"홍길동\",\"exhibitionStartDate\":\"2024-01-01\",\"exhibitionEndDate\":\"2024-03-31\"}}],\"cafeOwner\":{\"userId\":1,\"nickname\":\"김철수\",\"profileImageUrl\":\"https://example.com/image1.jpg\",\"ratingStats\":{\"totalReviews\":3,\"averageRating\":4.5}}}},\"message\":\"정상 처리\"}\n"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4041\", \"message\": \"존재하지 않는 카페입니다\"}"
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
    @GetMapping("/{cafeId}")
    public ResponseEntity<ResponseDto<CafeDetailData>> getCafeDetail(
            @Parameter(description = "조회할 카페 ID", required = true)
            @PathVariable Long cafeId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        CafeDetailData data = cafeService.getCafeDetail(userId, cafeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "카페 북마크 토글", description = "카페를 북마크에 추가하거나 제거합니다 (토글 방식). 모든 사용자가 사용 가능합니다. 북마크가 없으면 추가하고, 이미 있으면 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": {\"isBookmarked\" : true}, \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "토큰 누락", value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"),
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "회원 정보 없음", value = "{\"code\": \"US4041\", \"message\": \"존재하지 않는 회원입니다\"}"),
                                    @ExampleObject(name = "카페 정보 없음", value = "{\"code\": \"CF4041\", \"message\": \"존재하지 않는 카페입니다\"}")
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
    @PostMapping("/{cafeId}/bookmarks")
    public ResponseEntity<ResponseDto<BookmarkToggleResponse>> toggleBookmark(
            @Parameter(description = "북마크 토글할 카페 ID", required = true)
            @PathVariable Long cafeId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        BookmarkToggleResponse data = cafeBookmarkService.toggleBookmark(userId, cafeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "카페 카테고리 목록 조회", description = "카페 카테고리(인테리어 스타일) 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": {\"categories\": [ {\"categoryId\": 1,\"categoryName\": \"모던\"}, {\"categoryId\": 2,\"categoryName\": \"빈티지\"}, {\"categoryId\": 3,\"categoryName\": \"인더스트리얼\"} ]}, \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
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
    @GetMapping("/categories")
    public ResponseEntity<ResponseDto<CategoryListData>> getAllCafeCategories() {
        CategoryListData data = cafeCategoryService.getAllCafeCategories();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(
            summary = "슬롯의 전시 가능일 조회",
            description = "특정 슬롯의 전시 가능한 날짜 목록을 조회합니다.\n\n" +
                    "**주요 기능:**\n" +
                    "- 조회 시작일로부터 30일 동안의 전시 가능한 날짜 목록 반환\n" +
                    "- startDate가 없으면 오늘 날짜 기준으로 조회\n" +
                    "- 승인됨(APPROVED) 또는 진행 중(ONGOING) 상태의 전시와 겹치지 않는 날짜만 반환\n" +
                    "- 과거 날짜 조회 시 에러 발생 (오늘 날짜 이전은 조회 불가)\n" +
                    "- 날짜 형식: yyyy-MM-dd\n\n" +
                    "**권한:**\n" +
                    "- 작가만 사용 가능\n\n"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "전시 가능일이 있는 경우",
                                            description = "조회 기간 내에 전시 가능한 날짜가 있는 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"dates\": [\"2025-11-14\", \"2025-11-15\", \"2025-11-16\", \"2025-11-20\", \"2025-11-21\", \"2025-11-22\", \"2025-11-23\", \"2025-12-01\", \"2025-12-02\", \"2025-12-03\"]}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "전시 가능일이 없는 경우",
                                            description = "조회 기간 내에 모든 날짜가 이미 예약된 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"dates\": []}, \"message\": \"정상 처리\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "과거 날짜 조회",
                                    description = "오늘 날짜 이전의 날짜로 조회하려는 경우",
                                    value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
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
                                    name = "작가 권한 필요",
                                    description = "작가가 아닌 사용자가 접근한 경우",
                                    value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "존재하지 않는 슬롯",
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
    @GetMapping("/slots/{slotId}/available-dates")
    @PreAuthorize("hasAnyRole('ARTIST', 'CAFE_OWNER')")
    public ResponseEntity<ResponseDto<SlotAvailableDatesResponse>> getSlotAvailableDates(
            @Parameter(description = "슬롯 ID", required = true)
            @PathVariable Long slotId,
            @Parameter(description = "조회 시작일 (yyyy-MM-dd 형식, 기본값: 오늘 날짜)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate
    ) {
        SlotAvailableDatesResponse data = cafeService.getSlotAvailableDates(slotId, startDate);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(
            summary = "전시 가능한 슬롯 목록 조회",
            description = "특정 카페의 특정 기간에 전시 가능한 슬롯 목록을 조회합니다.\n\n" +
                    "**조회 조건:**\n" +
                    "- DisplaySlot이 존재하는 슬롯만 포함\n" +
                    "- 지정된 기간에 승인됨(APPROVED) 또는 진행 중(ONGOING) 상태의 전시가 없는 슬롯만 반환\n" +
                    "**권한:**\n" +
                    "- 작가만 사용 가능\n\n"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "전시 가능한 슬롯이 있는 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"slots\": [{\"wallId\": 1, \"width\": 200.0, \"height\": 250.0, \"commissionRate\": 10.0, \"requirements\": \"홍보용 작가 소개 카드를 함께 제공해주세요.\"}, {\"wallId\": 2, \"width\": 150.0, \"height\": 200.0, \"commissionRate\": 5.0}]}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "전시 가능한 슬롯이 없는 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"slots\": []}, \"message\": \"정상 처리\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "날짜 형식 오류",
                                    value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "토큰 누락", value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"),
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "작가 권한 필요",
                                    value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "존재하지 않는 카페",
                                    value = "{\"code\": \"CF4041\", \"message\": \"존재하지 않는 카페입니다\"}"
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
    @GetMapping("/{cafeId}/available-slots")
    @PreAuthorize("hasAnyRole('ARTIST', 'CAFE_OWNER')")
    public ResponseEntity<ResponseDto<AvailableSlotsData>> getAvailableSlots(
            @Parameter(description = "카페 ID", required = true)
            @PathVariable Long cafeId,
            @Parameter(description = "조회 시작일 (yyyy-MM-dd 형식)", required = true)
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @Parameter(description = "조회 종료일 (yyyy-MM-dd 형식)", required = true)
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        AvailableSlotsData data = cafeService.getAvailableSlots(cafeId, startDate, endDate);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    private List<CafeSummary> createMockCafes(int count, Long startId, Double baseDistance) {
        String[] names = {"카페 여백", "아트 카페", "갤러리 카페", "모던 카페", "빈티지 카페",
                "미니멀 카페", "북 카페", "오션뷰 카페", "숲속 카페", "루프탑 카페",
                "감성 카페", "복합문화 카페", "전시 카페", "작가의 카페", "예술 공간",
                "창작 카페", "디자인 카페", "뮤지엄 카페", "아틀리에 카페", "스튜디오 카페"};
        String[] addresses = {"서울시 강남구 테헤란로 123", "서울시 종로구 인사동길 45",
                "서울시 마포구 홍익로 78", "서울시 용산구 이태원로 234",
                "서울시 성동구 성수이로 156"};
        String[][] categoriesList = {
                {"모던", "미니멀"}, {"빈티지", "클래식"}, {"인더스트리얼", "모던"},
                {"북유럽", "미니멀"}, {"레트로", "빈티지"}
        };
        double[] latitudes = {37.5665, 37.5796, 37.5559, 37.5323, 37.5490};
        double[] longitudes = {126.9780, 126.9769, 126.9265, 126.9903, 127.0700};


        return IntStream.range(0, count)
                .mapToObj(i -> {
                    long id = startId + i;
                    String name = names[i % names.length] + " #" + id;
                    String address = addresses[i % addresses.length];
                    double lat = latitudes[i % latitudes.length];
                    double lon = longitudes[i % longitudes.length];
                    String primaryImageUrl = "https://example.com/cafe" + (i + 1) + ".jpg";
                    int availableSlotCount = 1 + (i % 4);
                    int totalSlotCount = 3 + (i % 3);
                    Double distance = baseDistance != null ? baseDistance + (i * 0.3) : null;
                    List<String> categories = List.of(categoriesList[i % categoriesList.length]);

                    return new CafeSummary(
                            id, name, address, lat, lon, primaryImageUrl,
                            availableSlotCount, totalSlotCount, distance, categories
                    );
                })
                .toList();
    }
}
