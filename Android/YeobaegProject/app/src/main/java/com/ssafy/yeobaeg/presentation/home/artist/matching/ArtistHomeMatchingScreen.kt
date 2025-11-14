package com.ssafy.yeobaeg.presentation.home.artist.matching

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.cafe.RecommendedCafe
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.lottie.YeobaegLottieLoading
import com.ssafy.yeobaeg.presentation.common.util.formatPrice
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun ArtistHomeMatchingScreen(
    viewModel: ArtistHomeMatchingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMyArtworks()
    }

    BackHandler {
        viewModel.goBackStep()
    }

    when (state.currentStep) {
        MatchingStep.ARTWORK_SELECTION -> {
            ArtworkSelectionStep(
                state = state,
                onArtworkClick = viewModel::selectArtwork,
                onLoadMore = viewModel::loadMoreArtworks,
                onConfirm = viewModel::confirmArtworkSelection,
                onBackClick = viewModel::navigateBack
            )
        }
        MatchingStep.CAFE_SELECTION -> {
            CafeSelectionStep(
                state = state,
                onCafeClick = viewModel::navigateToCafe,
                onLoadMore = viewModel::loadMoreCafes,
                onBackClick = viewModel::goBackStep
            )
        }

        MatchingStep.LOADING_RECOMMEND -> YeobaegLottieLoading(listOf(
            "취향에 맞는 작품을 찾는 중…",
            "카페에 어울리는 작품을 추천하고 있어요…",
            "이용 패턴을 분석해 추천 중…"
        ))
    }
}

// ========== Step 1: 작품 선택 ==========
@Composable
private fun ArtworkSelectionStep(
    state: ArtistHomeMatchingUiState,
    onArtworkClick: (Long) -> Unit,
    onLoadMore: () -> Unit,
    onConfirm: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        YeobaegTopAppBar(
            title = "매칭할 작품 선택",
            onBackClick = onBackClick
        )

        ArtworkList(
            artworks = state.myArtworks,
            isLoading = state.isLoadingArtworks,
            isLoadingMore = state.isLoadingMoreArtworks,
            hasNext = state.artworkPagination?.hasNext ?: false,
            onItemClick = onArtworkClick,
            onLoadMore = onLoadMore,
            selectedArtworkId = state.selectedArtworkId,
            modifier = Modifier.weight(1f)
        )

        HorizontalDivider(thickness = 0.5.dp, color = YeobaegGray)

        SelectedArtworkBar(
            selectedArtwork = state.selectedArtwork,
            onConfirm = onConfirm,
            enabled = state.selectedArtworkId != null
        )
    }
}

@Composable
private fun SelectedArtworkBar(
    selectedArtwork: ArtworkSummary?,
    onConfirm: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (selectedArtwork != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = selectedArtwork.thumbnailUrl,
                    contentDescription = selectedArtwork.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = selectedArtwork.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row {
                        Text(
                            text = "${selectedArtwork.widthCm} x ${selectedArtwork.heightCm}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = selectedArtwork.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = YeobaegGray
                        )
                    }
                }
            }
        } else {
            Text(
                text = "작품을 선택하세요",
                modifier = Modifier.padding(start = 12.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }

        YeobaegButton(
            text = "다음",
            onClick = onConfirm,
            enabled = enabled,
            modifier = Modifier.padding(end = 12.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun ArtworkList(
    artworks: List<ArtworkSummary>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasNext: Boolean,
    onItemClick: (Long) -> Unit,
    onLoadMore: () -> Unit,
    selectedArtworkId: Long?,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState, hasNext, isLoadingMore) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            Pair(lastVisible, total)
        }
            .map { (last, total) -> last >= total - 3 && total > 0 }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && hasNext && !isLoadingMore && !isLoading) {
                    coroutineScope.launch { onLoadMore() }
                }
            }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        state = listState
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        itemsIndexed(artworks) { _, artwork ->
            ArtworkListItem(
                artwork = artwork,
                isSelected = selectedArtworkId == artwork.artworkId,
                onClick = { onItemClick(artwork.artworkId) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            if (isLoadingMore) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (isLoading && artworks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (artworks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "등록한 작품이 없습니다.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun ArtworkListItem(
    artwork: ArtworkSummary,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors().copy(containerColor = Color.White),
        border = if (isSelected) BorderStroke(2.dp, YeobaegDarkBrown) else null
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = artwork.thumbnailUrl,
                contentDescription = artwork.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artwork.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "사이즈: ${artwork.widthCm} x ${artwork.heightCm}(cm)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "무게: 2kg", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    Text(text = "2025", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = artwork.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = YeobaegGray
                    )
                    Text(
                        text = artwork.price.formatPrice(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

// ========== Step 2: 카페 선택 ==========
@Composable
private fun CafeSelectionStep(
    state: ArtistHomeMatchingUiState,
    onCafeClick: (Long) -> Unit,
    onLoadMore: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        YeobaegTopAppBar(
            title = "카페 추천 결과",
            onBackClick = onBackClick
        )

        CafeList(
            cafes = state.cafes,
            isLoading = state.isLoadingCafes,
            isLoadingMore = state.isLoadingMoreCafes,
            hasNext = state.cafePagination?.hasNext ?: false,
            onItemClick = onCafeClick,
            onLoadMore = onLoadMore,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CafeList(
    cafes: List<RecommendedCafe>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasNext: Boolean,
    onItemClick: (Long) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState, hasNext, isLoadingMore) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            Pair(lastVisible, total)
        }
            .map { (last, total) -> last >= total - 3 && total > 0 }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && hasNext && !isLoadingMore && !isLoading) {
                    coroutineScope.launch { onLoadMore() }
                }
            }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        state = listState
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        itemsIndexed(cafes) { _, cafe ->
            CafeCard(
                modifier = Modifier.padding(vertical = 8.dp),
                imageUrl = cafe.primaryImageUrl,
                title = cafe.cafeName,
                address = cafe.address,
                rating = cafe.totalReviewScore,
                tags = cafe.slots.map { it.wallName to !it.isExhibiting },
                onTagClick = {},
                onClick = { onItemClick(cafe.cafeId) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            if (isLoadingMore) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (isLoading && cafes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (cafes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "매칭 가능한 카페가 없습니다.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}