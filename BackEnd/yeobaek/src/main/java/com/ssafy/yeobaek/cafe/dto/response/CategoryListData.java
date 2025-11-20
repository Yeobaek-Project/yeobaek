package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "카페 카테고리 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListData {
    @Schema(description = "카페 카테고리 목록")
    private List<Category> categories;
}
