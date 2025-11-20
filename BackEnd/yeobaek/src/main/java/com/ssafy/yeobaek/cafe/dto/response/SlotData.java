package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "전시 슬롯 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SlotData {
    @Schema(description = "전시 슬롯 정보")
    private Slot slot;
}
