package com.ssafy.yeobaek.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "비밀번호 재설정")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
    @Schema(description = "이메일로 받은 비밀번호 재설정 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    private String token;

    @Schema(description = "새 비밀번호 (8자 이상, 영문+숫자+특수문자 조합)", example = "NewPassword123!", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
             message = "INVALID_PASSWORD_FORMAT")
    private String newPassword;

    @Schema(description = "새 비밀번호 확인", example = "NewPassword123!", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    private String newPasswordConfirm;
}
