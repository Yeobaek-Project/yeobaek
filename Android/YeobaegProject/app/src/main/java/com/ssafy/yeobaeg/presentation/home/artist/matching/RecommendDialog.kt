package com.ssafy.yeobaeg.presentation.home.artist.matching

import androidx.compose.ui.graphics.Color
import com.ssafy.yeobaeg.domain.model.cafe.CafeSlot
import com.ssafy.yeobaeg.domain.model.cafe.RecommendedCafe
import kotlin.collections.lastIndex
import kotlin.math.absoluteValue

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import kotlinx.coroutines.launch

// ---------- 색상: 화이트 배경 + 브라운/골드 포인트 ----------

// 카드/배경
private val CardBackground = Color(0xFFFFFFFF)
private val CardStroke = Color(0xFFE5E0DA)

// 텍스트
private val TextOnCardPrimary = Color(0xFF5A4533)      // 진한 브라운
private val TextOnCardSecondary = Color(0xFF8B6E57)    // 보조 브라운
private val TextOnCardTertiary = Color(0xFFB0B0B0)

// 이미지 위 텍스트
private val TextOnImagePrimary = Color(0xFFFFFFFF)
private val TextOnImageSecondary = Color(0xFFE5E7EB)

// 포인트 컬러
private val PrimaryGold = Color(0xFFD6A05E)

// 슬롯 칩
private val SlotAvailableBg = Color(0xFFEBDDCB)        // 전시 가능
private val SlotAvailableBorder = Color(0xFFD6A05E)
private val SlotUnavailableBg = Color(0xFFF3F3F3)      // 전시 중
private val SlotUnavailableBorder = Color(0xFFD1D1D1)

// 인디케이터
private val IndicatorActive = PrimaryGold
private val IndicatorInactive = Color(0xFFDDDDDD)

@Composable
fun RecommendedCafePopup(
    recommendedCafes: List<RecommendedCafe>,
    startIndex: Int,
    onDetailClick: (cafeId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recommendedCafes.isEmpty()) return

    var currentIndex by remember {
        mutableIntStateOf(startIndex.coerceIn(0, recommendedCafes.lastIndex))
    }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun animateToPage(targetIndex: Int) {
        scope.launch {
            val direction = if (targetIndex > currentIndex) -1f else 1f
            offsetX.animateTo(
                targetValue = direction * 1000f,
                animationSpec = tween(durationMillis = 300)
            )
            currentIndex = targetIndex
            offsetX.snapTo(0f)
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 이전 카페 (왼쪽 뒤)
        if (currentIndex > 0) {
            CafeCard(
                cafe = recommendedCafes[currentIndex - 1],
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 40.dp)
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = -1000f + offsetX.value
                        val progress = (offsetX.value / 1000f).coerceIn(0f, 1f)
                        scaleX = 0.85f + (0.15f * progress)
                        scaleY = 0.85f + (0.15f * progress)
                        alpha = 0.3f + (0.7f * progress)
                    }
                    .zIndex(1f),
                onDetailClick = onDetailClick
            )
        }

        // 다음 카페 (오른쪽 뒤)
        if (currentIndex < recommendedCafes.lastIndex) {
            CafeCard(
                cafe = recommendedCafes[currentIndex + 1],
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 40.dp)
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = 1000f + offsetX.value
                        val progress = (-offsetX.value / 1000f).coerceIn(0f, 1f)
                        scaleX = 0.85f + (0.15f * progress)
                        scaleY = 0.85f + (0.15f * progress)
                        alpha = 0.3f + (0.7f * progress)
                    }
                    .zIndex(1f),
                onDetailClick = onDetailClick
            )
        }

        // 현재 카페 (가운데, 드래그 가능)
        CafeCard(
            cafe = recommendedCafes[currentIndex],
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 40.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    translationX = offsetX.value
                    val scale = 1f - (offsetX.value.absoluteValue / 3000f)
                    scaleX = scale.coerceIn(0.85f, 1f)
                    scaleY = scale.coerceIn(0.85f, 1f)
                }
                .zIndex(2f)
                .pointerInput(currentIndex) {
                    detectDragGestures(
                        onDragEnd = {
                            scope.launch {
                                when {
                                    offsetX.value < -200f && currentIndex < recommendedCafes.lastIndex -> {
                                        animateToPage(currentIndex + 1)
                                    }
                                    offsetX.value > 200f && currentIndex > 0 -> {
                                        animateToPage(currentIndex - 1)
                                    }
                                    else -> {
                                        offsetX.animateTo(0f, animationSpec = tween(200))
                                    }
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newOffset = offsetX.value + dragAmount.x
                                val minDrag = if (currentIndex < recommendedCafes.lastIndex) -500f else -100f
                                val maxDrag = if (currentIndex > 0) 500f else 100f
                                offsetX.snapTo(newOffset.coerceIn(minDrag, maxDrag))
                            }
                        }
                    )
                },
            onDetailClick = onDetailClick
        )

        // 페이지 인디케이터
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .zIndex(3f),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(recommendedCafes.size) { index ->
                val isSelected = index == currentIndex
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

        // 좌우 화살표 (원하면 지워도 됨)
        if (recommendedCafes.size > 1) {
            IconButton(
                onClick = {
                    if (currentIndex > 0) animateToPage(currentIndex - 1)
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .border(1.dp, CardStroke, CircleShape)
                    .zIndex(4f)
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "이전",
                    tint = TextOnCardPrimary
                )
            }

            IconButton(
                onClick = {
                    if (currentIndex < recommendedCafes.lastIndex) animateToPage(currentIndex + 1)
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .border(1.dp, CardStroke, CircleShape)
                    .zIndex(4f)
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "다음",
                    tint = TextOnCardPrimary
                )
            }
        }
    }
}

@Composable
private fun CafeCard(
    cafe: RecommendedCafe,
    modifier: Modifier = Modifier,
    onDetailClick: (cafeId: Long) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground,
            contentColor = TextOnCardPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 상단 이미지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
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
                                    maxLines = 1,
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
