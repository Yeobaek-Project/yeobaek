package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "내 카페 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyCafe {

    @Schema(description = "카페 ID", example = "1")
    private Long cafeId;

    @Schema(description = "매장명", example = "카페 여백")
    private String cafeName;

    @Schema(description = "카페 주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "대표 이미지 URL", example = "https://example.com/cafe1.jpg")
    private String primaryImageUrl;

    @Schema(description = "전시 슬롯 수", example = "5")
    private Long totalSlots;

    @Schema(description = "전시 중인 슬롯 수", example = "3")
    private Long exhibitingSlots;

    @Schema(description = "협업 작가 수", example = "15")
    private Long collaboratingArtists;

    @Schema(description = "전시 슬롯 정보")
    private List<Slot> displaySlots;
}
