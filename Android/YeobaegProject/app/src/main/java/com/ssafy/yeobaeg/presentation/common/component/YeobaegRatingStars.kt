package com.ssafy.yeobaeg.presentation.common.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

/**
 * 별점을 표시하는 공통 컴포넌트
 *
 * @param rating 0.0 ~ 5.0 사이의 별점 (Int 또는 Double 모두 지원)
 * @param starSize 별 크기 (dp)
 * @param tint 별 색상 (기본값: 노란색)
 * @param animated 애니메이션 활성화 여부 (기본값: false)
 *
 * 사용 예시:
 * ```
 * YeobaegRatingStars(rating = 4.8)  // ★★★★⯨
 * YeobaegRatingStars(rating = 5)    // ★★★★★
 * YeobaegRatingStars(rating = 3.2)  // ★★★☆☆
 * YeobaegRatingStars(rating = 4.8, animated = true)  // 애니메이션 포함
 * ```
 */
@Composable
fun YeobaegRatingStars(
    rating: Number,
    modifier: Modifier = Modifier,
    starSize: Int = 20,
    tint: Color = Color(0xFFFFC107),
    animated: Boolean = false
) {
    val ratingDouble = rating.toDouble().coerceIn(0.0, 5.0)
    val animatedRating = remember { Animatable(0f) }

    LaunchedEffect(ratingDouble, animated) {
        if (animated) {
            animatedRating.animateTo(
                targetValue = ratingDouble.toFloat(),
                animationSpec = tween(durationMillis = 1000)
            )
        } else {
            animatedRating.snapTo(ratingDouble.toFloat())
        }
    }

    val currentRating = animatedRating.value.toDouble()
    val totalStarWidth = (starSize + 2) * 5 - 2 // 별 5개 + 간격 4개

    Box(modifier = modifier) {
        // 배경 회색 별 (빈 별)
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(starSize.dp)
                )
            }
        }

        // 전경 노란색 별 (채워진 별)
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.drawWithContent {
                val clipWidth = size.width * (currentRating / 5.0).toFloat()
                clipRect(right = clipWidth) {
                    this@drawWithContent.drawContent()
                }
            }
        ) {
            repeat(5) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(starSize.dp)
                )
            }
        }
    }
}
