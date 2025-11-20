package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "전시 가능한 슬롯 목록 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotsData {
    @Schema(description = "전시 가능한 슬롯 목록")
    private List<Slot> slots;
}
