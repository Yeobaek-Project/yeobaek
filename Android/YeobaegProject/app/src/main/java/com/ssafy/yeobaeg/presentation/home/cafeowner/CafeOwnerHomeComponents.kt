package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray

@Composable
fun CafeTitle(cafeName: String, cafeAddress: String) {
    val colors = LocalCafeOwnerColors.current
    Text(
        text = cafeName,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = colors.title
    )
    Text(
        text = cafeAddress,
        fontSize = 14.sp,
        color = colors.address
    )
}

@Composable
fun CafeDisplayStatus(displaySpace: String, display: String, collaboratingArtist: String) {
    val colors = LocalCafeOwnerColors.current
    Row(
        modifier = Modifier.fillMaxWidth(). padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displaySpace,
                fontSize = 16.sp,
                color = colors.metricSpace
            )
            Text(
                text = "전시 공간",
                fontSize = 14.sp,
                color = YeobaegGray
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = display,
                fontSize = 16.sp,
                color = colors.metricDisplay
            )
            Text(
                text = "전시 중",
                fontSize = 14.sp,
                color = YeobaegGray
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = collaboratingArtist,
                fontSize = 16.sp,
                color = colors.metricArtist
            )
            Text(
                text = "협업 작가",
                fontSize = 14.sp,
                color = YeobaegGray
            )
        }
    }
}

