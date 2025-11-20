package com.ssafy.yeobaeg.presentation.home.cafeowner

import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwnerInfo


enum class CafeOwnerTab(val label: String) {
    PLAN("공간 도면"),
    STATUS("전시 현황")
}

data class CafeOwnerHomeUiState(
    val isLoading: Boolean = false,
    val cafeOwnerInfo: CafeOwnerInfo? = null,
    val selectedTab: CafeOwnerTab = CafeOwnerTab.PLAN
)
