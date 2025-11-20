package com.ssafy.yeobaek.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yeobaek.common.enums.Gender;
import com.ssafy.yeobaek.common.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "사용자 기본 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {
    @Schema(description = "회원 ID", example = "1")
    private Long userId;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "회원 유형(ARTIST,", example = "GENERAL")
    private UserRole userType;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "생년월일", example = "1990-01-01")
    private String birthDate;

    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @Schema(description = "북마크한 수(카페, 작품)", example = "37")
    private Integer bookmarkCount;
}
