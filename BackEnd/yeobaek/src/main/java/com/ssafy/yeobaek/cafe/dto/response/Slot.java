package com.ssafy.yeobaek.cafe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yeobaek.cafe.dto.Vec2;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "전시 슬롯 정보")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Slot {

    @Schema(description = "벽 ID", example = "1")
    private Long wallId;

    @Schema(description = "벽 이름", example = "1번 벽")
    private String wallName;

    @Schema(description = "시작점")
    private Vec2 start;

    @Schema(description = "끝점")
    private Vec2 end;

    @Schema(description = "벽면 폭 (cm)", example = "200")
    private Double width;

    @Schema(description = "벽면 높이 (cm)", example = "250")
    private Double height;

    @Schema(description = "전시 슬롯 여부", example = "true")
    private Boolean isDisplayable;

    @Schema(description = "전시 진행 여부", example = "true")
    private Boolean isExhibiting;

    @Schema(description = "수수료율", example = "15.0")
    private Double commissionRate;

    @Schema(description = "기타 요청 사항", example = "홍보용 작가 소개 카드 또는 작품 설명 자료를 함께 제공해주세요.")
    private String requirements;

    @Schema(description = "현재 전시 정보 (전시 중/예정인 경우만)")
    private ExhibitionInfo currentExhibition;
}
