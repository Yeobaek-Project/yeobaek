package com.ssafy.yeobaeg.presentation.common.util

fun Int.formatPrice(): String = "%,d원".format(this)

fun Double.removeTrailingZero(): String {
    val s = toString()
    return if (s.endsWith(".0")) s.dropLast(2) else s
}