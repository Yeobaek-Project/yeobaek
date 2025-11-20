package com.ssafy.yeobaek.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "비밀번호 재설정 요청 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestData {
    @Schema(description = "이메일이 발송된 주소", example = "user@example.com")
    private String email;

    @Schema(description = "인증 링크 만료 시간 (YYYY-MM-DD HH:mm:ss)", example = "2025-10-29 15:30:00")
    private String expiresAt;
}
