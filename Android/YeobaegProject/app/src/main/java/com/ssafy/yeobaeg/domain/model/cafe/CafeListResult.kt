package com.ssafy.yeobaeg.domain.model.cafe

import com.ssafy.yeobaeg.domain.model.Pagination

/**
 * 카페 목록 조회 결과
 */
data class CafeListResult(
    val cafes: List<CafeSummary>,
    val pagination: Pagination
)

