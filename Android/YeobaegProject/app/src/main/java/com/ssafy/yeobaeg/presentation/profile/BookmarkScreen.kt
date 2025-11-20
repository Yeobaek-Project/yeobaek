package com.ssafy.yeobaeg.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.profile.BookmarkCafeSummary
import com.ssafy.yeobaeg.presentation.common.component.PaginatedList
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.util.noRippleClickable
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont


@Composable
fun BookmarkScreen(
    bookmarkViewModel: BookmarkViewModel = hiltViewModel(),
) {
    val uiState by bookmarkViewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    val artworks = uiState.artwork?.artworks ?: emptyList()
    val cafes = uiState.bookmarkCafe?.bookmarkCafes ?: emptyList()
    val tabs = listOf("작품", "카페")

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "저장 목록",
                onBackClick = bookmarkViewModel::navigateBack,
                actionIcon = null,
                onActionClick = {}
            )
        },
        containerColor = YeobaegWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegWhite)
                .padding(paddingValues)
        ) {
            // 탭
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = YeobaegWhite,
                contentColor = YeobaegBlack,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp)
                            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)),
                        color = YeobaegBrown
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontFamily = jansilFont,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) YeobaegBrown else YeobaegTextGray
                            )
                        },
                        selectedContentColor = YeobaegBrown
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 탭별 리스트
            when (selectedTab) {
                0 -> {
                    // 작품 탭: 로드 완료 후 빈 목록인 경우 빈 상태 표시
                    if (!uiState.isLoading && artworks.isEmpty() && uiState.artwork != null) {
                        EmptyBookmarkContent("저장한 작품이 없습니다")
                    } else {
                        ArtworkList(
                            items = artworks,
                            isLoading = uiState.isLoading,
                            hasNext = uiState.artwork?.pagination?.hasNext ?: false,
                            onLoadMore = { bookmarkViewModel.loadNextArtworkPage() },
                            onItemClick = bookmarkViewModel::navigateToArtworkDetail
                        )
                    }
                }

                1 -> {
                    // 카페 탭 선택 시 데이터가 없고 아직 로드하지 않았다면 자동 로드
                    val isInitialLoad = uiState.bookmarkCafe == null
                    if (isInitialLoad && !uiState.isLoading) {
                        LaunchedEffect(Unit) {
                            bookmarkViewModel.loadBookmarkCafes()
                        }
                    }

                    // 로드 완료 후 빈 목록인 경우 빈 상태 표시
                    if (!uiState.isLoading && cafes.isEmpty() && !isInitialLoad) {
                        EmptyBookmarkContent("저장한 카페가 없습니다")
                    } else {
                        CafeList(
                            items = cafes,
                            isLoading = uiState.isLoading,
                            hasNext = uiState.bookmarkCafe?.pagination?.hasNext ?: false,
                            onLoadMore = { bookmarkViewModel.loadNextCafePage() },
                            onItemClick = bookmarkViewModel::navigateToCafeDetail
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyBookmarkContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = YeobaegTextGray,
                fontFamily = jansilFont
            )
        }
    }
}

@Composable
fun ArtworkList(
    items: List<ArtworkSummary>,
    isLoading: Boolean,
    hasNext: Boolean,
    onLoadMore: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    PaginatedList(
        items = items,
        isLoading = isLoading,
        hasNext = hasNext,
        onLoadMore = onLoadMore
    ) { item ->
        SavedCard(
            title = item.title,
            des = item.artistName,
            imageUrl = item.thumbnailUrl,
            onItemClick = {onItemClick(item.artworkId)}
        )
    }
}

@Composable
fun CafeList(
    items: List<BookmarkCafeSummary>,
    isLoading: Boolean,
    hasNext: Boolean,
    onLoadMore: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    PaginatedList(
        items = items,
        isLoading = isLoading,
        hasNext = hasNext,
        onLoadMore = onLoadMore
    ) { item ->
        SavedCard(
            title = item.cafeName,
            des = item.address,
            imageUrl = item.primaryImageUrl,
            onItemClick = {onItemClick(item.cafeId)}
        )
    }
}

@Composable
fun SavedCard(
    title: String,
    des: String,
    imageUrl: String,
    onItemClick: () -> Unit = { }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(YeobaegWhite)
            .border(1.dp, YeobaegLightGray, RoundedCornerShape(16.dp))
            .padding(12.dp)
            .noRippleClickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = YeobaegBlack
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = des,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                color = YeobaegTextGray
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_fill_heart),
            contentDescription = "Heart",
            tint = Color(0xFFFF5A5A),
            modifier = Modifier.size(40.dp)
        )
    }
}

