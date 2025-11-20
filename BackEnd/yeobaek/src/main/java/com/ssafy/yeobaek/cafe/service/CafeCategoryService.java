package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.response.CategoryListData;
import com.ssafy.yeobaek.cafe.entity.CafeCategory;

import java.util.List;

public interface CafeCategoryService {

    /**
     * ID 목록으로 카테고리 목록 조회
     * @param categoryIds 카테고리 ID 목록
     * @return 카테고리 엔티티 목록
     */
    List<CafeCategory> findCategoriesByIds(List<Long> categoryIds);

    /**
     * 모든 카페 카테고리 목록 조회
     * @return 카페 카테고리 목록
     */
    CategoryListData getAllCafeCategories();
}
