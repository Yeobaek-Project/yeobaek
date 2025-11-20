package com.ssafy.yeobaeg.domain.model

enum class CommissionRate(
    val label: String,
    val minCommission: Double?,
    val maxCommission: Double?
) {
    UNDER_5("5% 이하", null, 5.0),
    RATE_6_10("6-10%", 6.0, 10.0),
    RATE_11_15("11-15%", 11.0, 15.0),
    OVER_16("16% 이상", 16.0, null)
}