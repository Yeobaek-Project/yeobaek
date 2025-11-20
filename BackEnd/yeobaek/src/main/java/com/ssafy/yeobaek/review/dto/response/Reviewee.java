package com.ssafy.yeobaek.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "평가 대상자 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reviewee {

    @Schema(description = "평가 대상자 이름", example = "김철수")
    private String name;

    @Schema(description = "평가 대상자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "평가 대상자 역할", example = "CAFE_OWNER")
    private String role;
}
