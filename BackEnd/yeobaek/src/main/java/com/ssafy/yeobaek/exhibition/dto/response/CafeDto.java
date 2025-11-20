package com.ssafy.yeobaek.exhibition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "카페 간략 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CafeDto {
    @Schema(description = "카페 ID", example = "1")
    private Long cafeId;

    @Schema(description = "카페명", example = "여백 갤러리")
    private String cafeName;

    @Schema(description = "카페 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;
}
