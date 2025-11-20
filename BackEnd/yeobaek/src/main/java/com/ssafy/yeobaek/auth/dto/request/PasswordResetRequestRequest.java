package com.ssafy.yeobaek.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "비밀번호 재설정 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestRequest {
    @Schema(description = "비밀번호를 재설정할 이메일 주소", example = "user@example.com", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @Email(message = "INVALID_EMAIL_FORMAT")
    private String email;
}
