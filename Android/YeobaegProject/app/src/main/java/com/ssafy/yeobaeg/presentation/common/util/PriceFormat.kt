package com.ssafy.yeobaeg.presentation.common.util

import java.text.NumberFormat
import java.util.Locale

fun formatPriceInput(input: String): String {
    // 숫자만 추출
    val digitsOnly = input.filter { it.isDigit() }
    if (digitsOnly.isEmpty()) return ""

    // 천 단위 콤마 추가
    val number = digitsOnly.toLongOrNull() ?: return ""
    return NumberFormat.getNumberInstance(Locale.KOREA).format(number)
}