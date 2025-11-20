package com.ssafy.yeobaeg.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.art.PopularArtwork
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBackground
import com.ssafy.yeobaeg.ui.theme.YeobaegIndicatorGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegMediumBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegRed
import com.ssafy.yeobaeg.ui.theme.YeobaegTextPrimary
import com.ssafy.yeobaeg.ui.theme.YeobaegTextSecondary
import com.ssafy.yeobaeg.ui.theme.YeobaegTextTertiary
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

@Composable
fun GeneralHomeScreen(
    viewModel: GeneralHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    GeneralHomeScreen(
        uiState = uiState,
        onCafeClick = viewModel::navigateToCafeDetail,
        onArtworkClick = viewModel::navigateToArtworkDetail,
        onErrorDismiss = viewModel::clearError
    )
}

@Composable
fun GeneralHomeScreen(
    uiState: GeneralHomeUiState,
    onCafeClick: (Long) -> Unit = {},
    onArtworkClick: (Long) -> Unit = {},
    onErrorDismiss: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = null,
                titleIcon = R.drawable.logo_title,
                onBackClick = {},
                showBackButton = false
            )        },
        containerColor = YeobaegWhite
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top=paddingValues.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // 인기 카페 섹션
                item {
                    Text(
                        text = "인기 카페",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                item {
                    val cafePagerState = rememberPagerState(pageCount = { 
                        (uiState.popularCafes.size + 2) / 3 
                    })
                    
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalPager(
                            state = cafePagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                        ) { page ->
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                val startIndex = page * 3
                                val cafesInPage = uiState.popularCafes.drop(startIndex).take(3)
                                
                                cafesInPage.forEach { cafe ->
                                    PopularCafeItem(
                                        cafe = cafe,
                                        onClick = { onCafeClick(cafe.cafeId) }
                                    )
                                }
                            }
                        }
                        
                        // 페이지 인디케이터
                        if (cafePagerState.pageCount > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(cafePagerState.pageCount) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(if (index == cafePagerState.currentPage) 8.dp else 6.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(
                                                if (index == cafePagerState.currentPage) 
                                                    YeobaegMediumBrown 
                                                else 
                                                    YeobaegIndicatorGray
                                            )
                                    )
                                    if (index < cafePagerState.pageCount - 1) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // 인기 작품 섹션
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "인기 작품",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(uiState.popularArtworks) { artwork ->
                            PopularArtworkItem(
                                artwork = artwork,
                                onClick = { onArtworkClick(artwork.artworkId) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // 로딩 인디케이터
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 에러 스낵바
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = onErrorDismiss) {
                            Text("확인")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralHomeTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "여백",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = YeobaegMediumBrown
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = YeobaegWhite
        ),
        windowInsets = WindowInsets(0.dp)
    )
}

@Composable
fun PopularCafeItem(
    cafe: CafeSummary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 카페 이미지
        AsyncImage(
            model = cafe.primaryImageUrl,
            contentDescription = cafe.cafeName,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(YeobaegBackground),
            contentScale = ContentScale.Crop
        )

        // 카페 정보
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = cafe.cafeName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = YeobaegTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = YeobaegTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = cafe.address.split(" ").take(2).joinToString(" "),
                    style = MaterialTheme.typography.bodySmall,
                    color = YeobaegTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = cafe.categories.firstOrNull() ?: "",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = YeobaegLightBrown,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${cafe.totalSlotCount}개 작품 전시 중",
                style = MaterialTheme.typography.bodySmall,
                color = YeobaegTextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PopularArtworkItem(
    artwork: PopularArtwork,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        // 작품 이미지
        AsyncImage(
            model = artwork.thumbnailUrl,
            contentDescription = artwork.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(YeobaegBackground),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 작품 정보
        Column(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = artwork.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = YeobaegTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "by ${artwork.artistName}",
                style = MaterialTheme.typography.bodySmall,
                color = YeobaegTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 카페명 (공간 항상 유지)
            Box(
                modifier = Modifier.height(20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                artwork.cafeName?.let { cafeName ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = YeobaegTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = cafeName,
                            style = MaterialTheme.typography.bodySmall,
                            color = YeobaegTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 가격과 좋아요
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₩${String.format("%,d", artwork.price)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = YeobaegTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = YeobaegRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${artwork.bookmarkCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = YeobaegTextSecondary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeneralHomeScreenPreview() {
    MaterialTheme {
        GeneralHomeScreen(
            uiState = GeneralHomeUiState(
                popularCafes = listOf(
                    CafeSummary(
                        cafeId = 1,
                        cafeName = "Brew & Canvas",
                        address = "서울시 강남구 테헤란로 123",
                        primaryImageUrl = "",
                        availableSlotCount = 5,
                        totalSlotCount = 12,
                        distance = null,
                        categories = listOf("Urban Dreams Collection", "모던")
                    ),
                    CafeSummary(
                        cafeId = 2,
                        cafeName = "The Art Spot",
                        address = "서울시 강남구 역삼동 456",
                        primaryImageUrl = "",
                        availableSlotCount = 3,
                        totalSlotCount = 8,
                        distance = null,
                        categories = listOf("Nature & Serenity", "미니멀")
                    ),
                    CafeSummary(
                        cafeId = 3,
                        cafeName = "Moonlight Café",
                        address = "서울시 마포구 홍대입구 789",
                        primaryImageUrl = "",
                        availableSlotCount = 7,
                        totalSlotCount = 15,
                        distance = null,
                        categories = listOf("Contemporary Visions", "인더스트리얼")
                    )
                ),
                popularArtworks = listOf(
                    PopularArtwork(
                        artworkId = 1,
                        title = "Urban Dreams",
                        thumbnailUrl = "",
                        artistName = "김아티스트",
                        artistProfileImageUrl = "",
                        price = 450000,
                        category = "풍경화",
                        widthCm = 60,
                        heightCm = 80,
                        status = SlotStatus.AVAILABLE,
                        bookmarkCount = 234,
                        cafeName = "Brew & Canvas"
                    ),
                    PopularArtwork(
                        artworkId = 2,
                        title = "Morning Light",
                        thumbnailUrl = "",
                        artistName = "박화가",
                        artistProfileImageUrl = "",
                        price = 320000,
                        category = "추상화",
                        widthCm = 50,
                        heightCm = 70,
                        status = SlotStatus.EXHIBITING,
                        bookmarkCount = 189,
                        cafeName = null
                    )
                ),
                isLoading = false,
                error = null
            )
        )
    }
}
