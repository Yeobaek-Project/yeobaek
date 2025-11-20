package com.ssafy.yeobaek.auth.dto.response;

import com.ssafy.yeobaek.common.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "로그인 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginData {
    @Schema(description = "JWT Access Token (유효기간: 1시간)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT Refresh Token (유효기간: 7일)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "회원 ID(PK)", example = "12")
    private Long userId;

    @Schema(description = "회원 유형 (CAFE_OWNER, ARTIST, GENERAL)", example = "GENERAL")
    private UserRole userType;
}
