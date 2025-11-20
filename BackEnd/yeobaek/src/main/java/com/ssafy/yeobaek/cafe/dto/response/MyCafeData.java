package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "내 카페 조회 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyCafeData {

    @Schema(description = "내 카페 정보")
    private MyCafe myCafe;

}
