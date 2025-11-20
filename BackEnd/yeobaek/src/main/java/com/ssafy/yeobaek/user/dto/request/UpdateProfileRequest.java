package com.ssafy.yeobaek.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "프로필 정보 수정 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    @Schema(description = "닉네임 (2~15자)", example = "홍길동")
    @Size(min = 2, max = 15, message = "INVALID_PROFILE_NICKNAME_LENGTH")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "작가 소개 (ARTIST만, 최대 250자)", example = "현대 미술을 전공한 신진 작가입니다.")
    @Size(max = 250, message = "INVALID_INTRODUCTION_LENGTH")
    private String introduction;
}
