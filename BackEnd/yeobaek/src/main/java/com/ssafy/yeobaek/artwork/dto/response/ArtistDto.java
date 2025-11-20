package com.ssafy.yeobaek.artwork.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "작가 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtistDto {
    @Schema(description = "작가 회원 ID", example = "1")
    private Long userId;

    @Schema(description = "작가명", example = "홍길동")
    private String artistName;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "작가 소개", example = "현대 미술을 전공한 신진 작가입니다.")
    private String introduction;

    @Schema(description = "전체 작품 수", example = "15")
    private Integer artworkCount;

    @Schema(description = "현재 전시 중인 작품 수", example = "3")
    private Integer currentExhibitionCount;
}
