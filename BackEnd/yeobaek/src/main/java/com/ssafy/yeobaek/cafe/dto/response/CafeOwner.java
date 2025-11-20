package com.ssafy.yeobaek.cafe.dto.response;

import com.ssafy.yeobaek.user.dto.response.RatingStatsData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "카페 사장 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CafeOwner {
    @Schema(description = "카페 사장 회원 ID", example = "1")
    private Long userId;

    @Schema(description = "카페 사장 닉네임", example = "카페사장")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "평가 정보")
    private RatingStatsData ratingStats;
}
