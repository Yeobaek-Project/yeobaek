package com.ssafy.yeobaek.auth.controller;

import com.ssafy.yeobaek.auth.dto.request.*;
import com.ssafy.yeobaek.auth.dto.response.*;
import com.ssafy.yeobaek.auth.service.AuthService;
import com.ssafy.yeobaek.common.dto.ResponseDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Tag(name = "회원 관리", description = "회원 인증 및 관리 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "이메일 중복 검사", description = "회원가입 전 이메일 중복 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": {\"email\": \"user@example.com\", \"isAvailable\": true}, \"message\": \"정상 처리\"}"
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
                                            name = "이메일 형식 오류",
                                            value = "{\"code\": \"AU4004\", \"message\": \"이메일 형식이 올바르지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "필수 입력값 누락",
                                            value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}"
                                    )
                            }
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
    @PostMapping("/email/check")
    public ResponseEntity<ResponseDto<EmailCheckData>> checkEmail(@Valid @RequestBody EmailCheckRequest request) {
        EmailCheckData data = authService.checkEmailDuplicate(request.getEmail());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "회원가입", description = "사용자 계정을 생성하고 기본 정보를 등록합니다. userType에 따라 카페 사장, 작가, 일반 사용자로 구분됩니다.")
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
                                            name = "이미 사용 중인 이메일",
                                            value = "{\"code\": \"AU4001\", \"message\": \"이미 사용 중인 이메일입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "비밀번호 형식 오류",
                                            value = "{\"code\": \"AU4002\", \"message\": \"비밀번호는 8자 이상 20자 이하이어야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "비밀번호 불일치",
                                            value = "{\"code\": \"AU4003\", \"message\": \"비밀번호가 일치하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "이메일 형식 오류",
                                            value = "{\"code\": \"AU4004\", \"message\": \"이메일 형식이 올바르지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "회원 유형 불일치",
                                            value = "{\"code\": \"AU4006\", \"message\": \"유효하지 않은 회원 유형입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "닉네임 길이 오류",
                                            value = "{\"code\": \"AU4007\", \"message\": \"닉네임은 2~15자 이내여야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "생년월일 형식 오류",
                                            value = "{\"code\": \"AU4008\", \"message\": \"생년월일 형식이 올바르지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "성별 유형 불일치",
                                            value = "{\"code\": \"AU4009\", \"message\": \"유효하지 않은 성별입니다\"}"
                                    )
                            }
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
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created());
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 인증하고 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": {\"accessToken\": \"accessToken\", \"refreshToken\": \"refreshToken\", \"userId\": 12, \"userType\": \"GENERAL\"}, \"message\": \"정상 처리\"}"
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
                                            name = "이메일 형식 오류",
                                            value = "{\"code\": \"AU4004\", \"message\": \"이메일 형식이 올바르지 않습니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "비밀번호 불일치",
                                    value = "{\"code\": \"AU4011\", \"message\": \"비밀번호가 일치하지 않습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"AU4041\", \"message\": \"존재하지 않는 회원입니다\"}"
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
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginData>> login(@Valid @RequestBody LoginRequest request) {
        LoginData data = authService.login(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃 처리합니다. 서버에서 해당 사용자의 refreshToken을 무효화합니다.")
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
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
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
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout() {
        authService.logout();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }

    @Operation(summary = "Access Token 재발급", description = "refreshToken을 사용하여 새로운 accessToken과 refreshToken을 발급합니다. 토큰 갱신(rotate) 방식으로 refreshToken도 함께 갱신됩니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": {\"accessToken\": \"accessToken\", \"refreshToken\": \"refreshToken\"}, \"message\": \"정상 처리\"}"
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
    @PostMapping("/reissue")
    public ResponseEntity<ResponseDto<TokenData>> reissue(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader
    ) {
        // Bearer 토큰 추출
        String refreshToken = authorizationHeader.replace("Bearer ", "");

        // 새로운 토큰 발급
        TokenData data = authService.reissue(refreshToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "비밀번호 재설정 요청", description = "비밀번호 재설정을 위한 이메일 인증 링크를 발송합니다. 인증 링크는 30분간 유효합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": {\"email\": \"user@example.com\", \"expiresAt\": \"2024-12-31 12:30:00\"}, \"message\": \"정상 처리\"}"
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
                                            name = "이메일 형식 오류",
                                            value = "{\"code\": \"AU4004\", \"data\": null, \"message\": \"이메일 형식이 올바르지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "필수 입력값 누락",
                                            value = "{\"code\": \"S4001\", \"data\": null, \"message\": \"필수 입력값이 누락되었습니다\"}"
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
                                    value = "{\"code\": \"AU4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}"
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
    @PostMapping("/password/reset-request")
    public ResponseEntity<ResponseDto<PasswordResetRequestData>> passwordResetRequest(
            @Valid @RequestBody PasswordResetRequestRequest request
    ) {
        // Mock 데이터: 이메일과 만료시간 반환
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PasswordResetRequestData data = new PasswordResetRequestData(
                request.getEmail(),
                expiresAt.format(formatter)
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일 인증 후 새로운 비밀번호로 재설정합니다. 재설정 토큰은 30분간 유효합니다.")
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
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4014\", \"data\": null, \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
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
    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDto<Void>> passwordReset(@Valid @RequestBody PasswordResetRequest request) {
        // Mock 데이터: 성공 응답 반환 (data는 null)
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }
}
