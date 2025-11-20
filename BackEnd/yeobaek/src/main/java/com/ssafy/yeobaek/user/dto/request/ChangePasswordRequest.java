package com.ssafy.yeobaek.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "패스워드 변경 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @Schema(description = "현재 패스워드", example = "CurrentPassword123!", required = true)
    private String currentPassword;

    @Schema(description = "새 패스워드", example = "NewPassword123!", required = true)
    private String newPassword;

    @Schema(description = "새 패스워드 확인", example = "NewPassword123!", required = true)
    private String newPasswordConfirm;
}
