package com.ssafy.yeobaeg.presentation.home.cafeowner.matching

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.util.formatPrice
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private val CardBrown = Color(0xFF5A4533)
private val PrimaryGold = Color(0xFFD6A05E)

private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFE5E7EB)

private val IndicatorActive = PrimaryGold
private val IndicatorInactive = Color(0xFF3C3A39)

@Composable
fun RecommendedArtworkCards(
    artworks: List<ArtworkSummary>,
    startIndex: Int,
    onArtworkClick: (artworkId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (artworks.isEmpty()) return

    var currentIndex by remember {
        mutableIntStateOf(startIndex.coerceIn(0, artworks.lastIndex))
    }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // 다음/이전 작품으로 전환하는 함수
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
        // 이전 작품 (왼쪽)
        if (currentIndex > 0) {
            ArtworkCard(
                artwork = artworks[currentIndex - 1],
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
                onArtworkClick = onArtworkClick
            )
        }

        // 다음 작품 (오른쪽)
        if (currentIndex < artworks.lastIndex) {
            ArtworkCard(
                artwork = artworks[currentIndex + 1],
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
                onArtworkClick = onArtworkClick
            )
        }

        // 현재 작품 (드래그 가능)
        ArtworkCard(
            artwork = artworks[currentIndex],
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
                                    offsetX.value < -200f && currentIndex < artworks.lastIndex -> {
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
                                val minDrag = if (currentIndex < artworks.lastIndex) -500f else -100f
                                val maxDrag = if (currentIndex > 0) 500f else 100f
                                offsetX.snapTo(newOffset.coerceIn(minDrag, maxDrag))
                            }
                        }
                    )
                },
            onArtworkClick = onArtworkClick
        )

        // 페이지 인디케이터 (카드 밖, 하단 중앙)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .zIndex(3f),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(artworks.size) { index ->
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

        // 좌우 화살표
        if (artworks.size > 1) {
            IconButton(
                onClick = {
                    if (currentIndex > 0) {
                        animateToPage(currentIndex - 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .zIndex(3f)
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "이전",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {
                    if (currentIndex < artworks.lastIndex) {
                        animateToPage(currentIndex + 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .zIndex(3f)
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "다음",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ArtworkCard(
    artwork: ArtworkSummary,
    modifier: Modifier = Modifier,
    onArtworkClick: (artworkId: Long) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 상단 이미지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            ) {
                AsyncImage(
                    model = artwork.thumbnailUrl,
                    contentDescription = artwork.title,
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
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        Text(
                            text = artwork.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = artwork.category,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 작품 상세 정보
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "사이즈",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = YeobaegGray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${artwork.widthCm} x ${artwork.heightCm} cm",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "가격",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = YeobaegGray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = artwork.price.formatPrice(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "무게",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = YeobaegGray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "2kg",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "제작년도",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = YeobaegGray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "2025",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                YeobaegButton(
                    text = "작품 상세보기",
                    onClick = { onArtworkClick(artwork.artworkId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                )
            }
        }
    }
}