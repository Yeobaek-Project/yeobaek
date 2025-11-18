package com.ssafy.yeobaeg.presentation.home.artist.matching

import androidx.compose.ui.graphics.Color
import com.ssafy.yeobaeg.domain.model.cafe.CafeSlot
import com.ssafy.yeobaeg.domain.model.cafe.RecommendedCafe
import kotlin.collections.lastIndex
import kotlin.math.abs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import kotlinx.coroutines.launch

// ---------- 색상: 화이트 배경 + 브라운/골드 포인트 ----------
private val CardBackground = Color(0xFFFFFFFF)
private val CardStroke = Color(0xFFE5E0DA)
private val TextOnCardPrimary = Color(0xFF5A4533)
private val TextOnCardSecondary = Color(0xFF8B6E57)
private val TextOnCardTertiary = Color(0xFFB0B0B0)
private val TextOnImagePrimary = Color(0xFFFFFFFF)
private val TextOnImageSecondary = Color(0xFFE5E7EB)
private val PrimaryGold = Color(0xFFD6A05E)
private val SlotAvailableBg = Color(0xFFEBDDCB)
private val SlotAvailableBorder = Color(0xFFD6A05E)
private val SlotUnavailableBg = Color(0xFFF3F3F3)
private val SlotUnavailableBorder = Color(0xFFD1D1D1)
private val IndicatorActive = PrimaryGold
private val IndicatorInactive = Color(0xFFDDDDDD)

@Composable
fun RecommendedCafePopup(
    recommendedCafes: List<RecommendedCafe>,
    onDetailClick: (cafeId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recommendedCafes.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { recommendedCafes.size }
    )
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 40.dp),
            pageSpacing = 16.dp
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

            CafeCard(
                cafe = recommendedCafes[page],
                pageOffset = pageOffset,
                onDetailClick = { cafeId ->
                    if (pagerState.currentPage == page) {
                        onDetailClick(cafeId)
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    }
                },
            )
        }

        // 페이지 인디케이터
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(recommendedCafes.size) { index ->
                val isSelected = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .width(if (isSelected) 18.dp else 10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (isSelected) IndicatorActive else IndicatorInactive
                        )
                )
            }
        }
    }
}
@Composable
private fun CafeCard(
    cafe: RecommendedCafe,
    pageOffset: Float,
    onDetailClick: (cafeId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val absOffset = abs(pageOffset).coerceIn(0f, 1f)
    val scale = lerp(0.9f, 1f, 1f - absOffset)
    val alpha = lerp(0.6f, 1f, 1f - absOffset)
    val shape = RoundedCornerShape(28.dp)

    // shadowElevation은 px 단위라 density 필요
    val density = androidx.compose.ui.platform.LocalDensity.current
    val elevationPx = with(density) {
        // 가운데 카드일수록 더 진한 그림자
        lerp(2.dp, 14.dp, 1f - absOffset).toPx()
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha

                // 그림자/모양을 그래픽 레이어에 직접 설정
                this.shape = shape
                clip = false            // 그림자가 모서리에서 안 잘리게
                shadowElevation = elevationPx
            }
            .clickable { onDetailClick(cafe.cafeId) },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = CardBackground,
            contentColor = TextOnCardPrimary
        ),
        // 여기서는 elevation 0으로 두고, shadow는 위 graphicsLayer에서만 처리
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 상단 이미지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                AsyncImage(
                    model = cafe.primaryImageUrl,
                    contentDescription = cafe.cafeName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = cafe.cafeName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextOnImagePrimary,
                                    fontSize = 24.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = cafe.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextOnImageSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = PrimaryGold
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", cafe.totalReviewScore),
                                    color = TextOnImagePrimary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // 상세 정보
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                if (cafe.categories.isNotEmpty()) {
                    Text(
                        text = cafe.categories.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextOnCardSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = "전시 슬롯 상태",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextOnCardPrimary
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cafe.slots.forEach { slot ->
                        SlotStatusChip(slot = slot)
                    }
                }

                Spacer(Modifier.height(20.dp))

                val hasAvailableSlot = cafe.slots.any { !it.isExhibiting }

                YeobaegButton(
                    text = "상세 보기",
                    onClick = { onDetailClick(cafe.cafeId) },
                    enabled = hasAvailableSlot,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                )
            }
        }
    }
}


@Composable
private fun SlotStatusChip(
    slot: CafeSlot,
    modifier: Modifier = Modifier
) {
    val isExhibiting = slot.isExhibiting

    val bgColor = if (isExhibiting) SlotUnavailableBg else SlotAvailableBg
    val borderColor = if (isExhibiting) SlotUnavailableBorder else SlotAvailableBorder
    val titleColor = if (isExhibiting) TextOnCardTertiary else TextOnCardPrimary
    val subLabel = if (isExhibiting) "전시 중" else "전시 가능"
    val subColor = if (isExhibiting) TextOnCardTertiary else PrimaryGold

    Column(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = slot.wallName,
            color = titleColor,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subLabel,
            color = subColor,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}