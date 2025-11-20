package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegGray

@Stable
data class CafeOwnerColors(
    val title: Color,
    val address: Color,
    val metricSpace: Color,
    val metricDisplay: Color,
    val metricArtist: Color
)

val RegisteredColors = CafeOwnerColors(
    title = Color.Black,
    address = YeobaegDarkGray,
    metricSpace = Color.Black,   // 전시공간 숫자
    metricDisplay = Color(0xFF16A34A), // 전시중 숫자
    metricArtist = Color(0xFF2563EB)   // 협업작가 숫자
)

val UnregisteredColors = CafeOwnerColors(
    title = YeobaegGray,
    address = YeobaegDarkGray,
    metricSpace = Color.Black,
    metricDisplay = Color.Black,
    metricArtist = Color.Black
)

val LocalCafeOwnerColors = staticCompositionLocalOf { RegisteredColors }
