package com.ssafy.yeobaeg.domain.model.cafe

/**
 * 카페 검색 필터 (작가 전용)
 */
data class CafeFilter(
    val minWidth: Int? = null,
    val maxWidth: Int? = null,
    val minHeight: Int? = null,
    val maxHeight: Int? = null,
    val minCommission: Double? = null,
    val maxCommission: Double? = null
) {
    /**
     * 필터가 설정되어 있는지 확인
     */
    fun isActive(): Boolean {
        return minWidth != null || maxWidth != null ||
                minHeight != null || maxHeight != null ||
                minCommission != null || maxCommission != null
    }
}

