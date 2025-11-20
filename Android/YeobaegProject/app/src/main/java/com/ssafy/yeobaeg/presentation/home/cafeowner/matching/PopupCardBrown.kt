package com.ssafy.yeobaeg.presentation.home.cafeowner.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.util.formatPrice
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import kotlin.math.abs

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

    val safeStart = startIndex.coerceIn(0, artworks.lastIndex)

    val pagerState = rememberPagerState(
        initialPage = safeStart,
        pageCount = { artworks.size }
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 40.dp),
            pageSpacing = 16.dp
        ) { page ->
            val pageOffset =
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

            ArtworkCard(
                artwork = artworks[page],
                pageOffset = pageOffset,
                onArtworkClick = onArtworkClick
            )
        }

        // 페이지 인디케이터
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(artworks.size) { index ->
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
private fun ArtworkCard(
    artwork: ArtworkSummary,
    pageOffset: Float,
    modifier: Modifier = Modifier,
    onArtworkClick: (artworkId: Long) -> Unit
) {
    val absOffset = abs(pageOffset).coerceIn(0f, 1f)
    val scale = lerp(0.9f, 1f, 1f - absOffset)
    val alpha = lerp(0.6f, 1f, 1f - absOffset)
    val shape = RoundedCornerShape(28.dp)

    val density = androidx.compose.ui.platform.LocalDensity.current
    val elevationPx = with(density) {
        lerp(4.dp, 16.dp, 1f - absOffset).toPx()
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha

                this.shape = shape
                clip = false
                shadowElevation = elevationPx
            }
            .clickable { onArtworkClick(artwork.artworkId) },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 상단 이미지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
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
                            maxLines = 2,
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
