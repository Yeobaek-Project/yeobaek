package com.ssafy.yeobaek.user.controller;

import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import com.ssafy.yeobaek.user.dto.request.ChangePasswordRequest;
import com.ssafy.yeobaek.user.dto.request.DeleteAccountRequest;
import com.ssafy.yeobaek.user.dto.request.UpdateProfileRequest;
import com.ssafy.yeobaek.user.dto.response.UserProfileData;
import com.ssafy.yeobaek.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Tag(name = "사용자 프로필", description = "사용자 프로필 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다. userType에 따라 반환되는 정보가 다릅니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "작가(ARTIST) 프로필",
                                            value = "{\"code\": \"S0000\", \"data\": {\"user\": {\"userId\": 1, \"email\": \"artist@example.com\", \"nickname\": \"작가 홍길동\", \"userType\": \"ARTIST\", \"profileImageUrl\": \"https://example.com/artist-profile.jpg\", \"birthDate\": \"1990-01-01\", \"gender\": \"MALE\", \"bookmarkCount\": 37}, \"artist\": {\"introduction\": \"현대 미술을 전공한 신진 작가입니다. 주로 추상화와 설치미술 작업을 하고 있습니다.\", \"artworkCount\": 12, \"exhibitingCount\": 3, \"ratingStats\": {\"totalReviews\": 24, \"averageRating\": 4.3}}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "카페 사장(CAFE_OWNER) 프로필",
                                            value = "{\"code\": \"S0000\", \"data\": {\"user\": {\"userId\": 2, \"email\": \"cafe@example.com\", \"nickname\": \"카페사장\", \"userType\": \"CAFE_OWNER\", \"profileImageUrl\": \"https://example.com/cafe-profile.jpg\", \"birthDate\": \"1985-05-15\", \"gender\": \"FEMALE\", \"bookmarkCount\": 37}, \"cafe\": {\"cafeId\": 1, \"cafeName\": \"아트 갤러리 카페\", \"address\": \"서울시 강남구 테헤란로 123\", \"description\": \"작가들의 작품을 전시하고 감상할 수 있는 복합 문화 공간입니다.\", \"contact\": \"02-1234-5678\", \"businessHoursStart\": \"09:00\", \"businessHoursEnd\": \"22:00\", \"ratingStats\": {\"totalReviews\": 156, \"averageRating\": 4.7}}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "일반 사용자(GENERAL) 프로필",
                                            value = "{\"code\": \"S0000\", \"data\": {\"user\": {\"userId\": 3, \"email\": \"user@example.com\", \"nickname\": \"일반사용자\", \"userType\": \"GENERAL\", \"profileImageUrl\": \"https://example.com/user-profile.jpg\", \"birthDate\": \"1995-03-20\", \"gender\": \"MALE\", \"bookmarkCount\": 37}}, \"message\": \"정상 처리\"}"
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
                    description = "존재하지 않는 회원",
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
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<UserProfileData>> getMyProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        UserProfileData data = userService.getMyProfile(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "특정 사용자 프로필 조회", description = "특정 사용자의 공개 프로필 정보를 조회합니다. userType에 따라 반환되는 정보가 다릅니다.\n\n **공개 정보: user 정보 중 userId, nickname, userType, profileImageUrl만 공개됩니다. artist와 cafe 정보는 전부 공개됩니다.**")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "작가(ARTIST) 프로필",
                                            value = "{\"code\": \"S0000\", \"data\": {\"user\": {\"userId\": 1, \"nickname\": \"작가 홍길동\", \"userType\": \"ARTIST\", \"profileImageUrl\": \"https://example.com/artist-profile.jpg\"}, \"artist\": {\"introduction\": \"현대 미술을 전공한 신진 작가입니다. 주로 추상화와 설치미술 작업을 하고 있습니다.\", \"artworkCount\": 12, \"exhibitingCount\": 3, \"ratingStats\": {\"totalReviews\": 24, \"averageRating\": 4.3}}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "카페 사장(CAFE_OWNER) 프로필",
                                            value = "{\"code\": \"S0000\", \"data\": {\"user\": {\"userId\": 2, \"nickname\": \"카페사장\", \"userType\": \"CAFE_OWNER\", \"profileImageUrl\": \"https://example.com/cafe-profile.jpg\"}, \"cafe\": {\"cafeId\": 1, \"cafeName\": \"아트 갤러리 카페\", \"address\": \"서울시 강남구 테헤란로 123\", \"description\": \"작가들의 작품을 전시하고 감상할 수 있는 복합 문화 공간입니다.\", \"contact\": \"02-1234-5678\", \"businessHoursStart\": \"09:00\", \"businessHoursEnd\": \"22:00\", \"ratingStats\": {\"totalReviews\": 156, \"averageRating\": 4.7}}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "일반 사용자(GENERAL) 프로필",
                                            value = "{\"code\": \"S0000\", \"data\": {\"user\": {\"userId\": 3, \"nickname\": \"일반사용자\", \"userType\": \"GENERAL\", \"profileImageUrl\": \"https://example.com/user-profile.jpg\"}}, \"message\": \"정상 처리\"}"
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
                    description = "존재하지 않는 회원",
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
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto<UserProfileData>> getUserProfile(
            @Parameter(description = "조회할 회원 ID", required = true)
            @PathVariable Long userId
    ) {
        UserProfileData data = userService.getUserProfile(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "프로필 정보 수정", description = "현재 로그인한 사용자의 프로필 정보를 수정합니다.")
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
                                    @ExampleObject(
                                            name = "닉네임 길이 오류",
                                            value = "{\"code\": \"US4002\", \"message\": \"닉네임은 2자 이상 15자 이하이어야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작가 소개 길이 오류",
                                            value = "{\"code\": \"US4003\", \"message\": \"작가 소개는 250자 이하이어야 합니다\"}"
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
                    description = "존재하지 않는 회원",
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
    @PatchMapping("/me")
    public ResponseEntity<ResponseDto<Void>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.updateProfile(userId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }

    @Operation(summary = "패스워드 변경", description = "현재 로그인한 사용자의 패스워드를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": null, \"message\": \"정상 처리\"}"
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
                                            name = "비밀번호 형식 오류",
                                            value = "{\"code\": \"AU4002\", \"data\": null, \"message\": \"비밀번호는 8자 이상 20자 이하이어야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "비밀번호 불일치",
                                            value = "{\"code\": \"AU4003\", \"data\": null, \"message\": \"비밀번호가 일치하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "필수 입력값 누락",
                                            value = "{\"code\": \"S4001\", \"data\": null, \"message\": \"필수 입력값이 누락되었습니다\"}"
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
                                            name = "현재 패스워드 불일치",
                                            value = "{\"code\": \"US4011\", \"data\": null, \"message\": \"현재 패스워드가 일치하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"US4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @PutMapping("/me/password")
    public ResponseEntity<ResponseDto<Void>> changePassword(
            @RequestBody ChangePasswordRequest request
    ) {
        // Mock 데이터: 성공 응답 반환
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다. 회원 관련 데이터가 모두 삭제되며, 복구할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": null, \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "필수 입력값 누락",
                                    value = "{\"code\": \"S4001\", \"data\": null, \"message\": \"필수 입력값이 누락되었습니다\"}"
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
                                            name = "현재 패스워드 불일치",
                                            value = "{\"code\": \"US4011\", \"data\": null, \"message\": \"현재 패스워드가 일치하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"US4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @DeleteMapping("/me")
    public ResponseEntity<ResponseDto<Void>> deleteAccount(
            @RequestBody DeleteAccountRequest request
    ) {
        // Mock 데이터: 성공 응답 반환
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }
}
