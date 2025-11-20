package com.ssafy.yeobaek.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "회원 탈퇴 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAccountRequest {
    @Schema(description = "본인 확인을 위한 현재 패스워드", example = "Password123!", required = true)
    private String password;
}
