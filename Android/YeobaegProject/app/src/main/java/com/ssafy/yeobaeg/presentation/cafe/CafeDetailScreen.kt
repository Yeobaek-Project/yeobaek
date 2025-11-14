package com.ssafy.yeobaeg.presentation.cafe

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.presentation.cafe.components.*
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeDetailScreen(
    cafeId: Long,
    viewModel: CafeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()
    val sessionStart = remember(cafeId) { SystemClock.elapsedRealtime() }
    var maxPct by remember(cafeId) { mutableIntStateOf(0) }

    LaunchedEffect(cafeId) {
        viewModel.loadCafeDetail(cafeId)
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            val totalItemsCount = layoutInfo.totalItemsCount

            // 엣지 케이스 처리: 아이템이 없거나, 보이는 아이템 정보가 없으면 0%
            if (totalItemsCount == 0 || visibleItemsInfo.isEmpty()) {
                return@snapshotFlow 0
            }

            // 1. 평균 아이템 높이 계산
            val avgItemHeight = visibleItemsInfo.sumOf { it.size }.toFloat() / visibleItemsInfo.size

            // 2. 현재까지 스크롤된 총 픽셀량 계산 (가장 정밀한 부분)
            // (지나온 아이템들의 추정 높이) + (현재 첫 아이템이 스크롤된 픽셀)
            val totalScrolledPixels = (listState.firstVisibleItemIndex * avgItemHeight) + listState.firstVisibleItemScrollOffset

            // 3. 전체 콘텐츠의 추정 높이 계산
            val totalContentHeight = totalItemsCount * avgItemHeight

            // 4. 뷰포트(화면) 높이
            val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset

            // 5. 스크롤 가능한 전체 픽셀 범위
            // (전체 콘텐츠 높이에서 화면 높이를 빼야 실제 스크롤 가능한 범위가 나옴)
            val scrollableRange = (totalContentHeight - viewportHeight).coerceAtLeast(1f)

            // 6. 최종 진행률 계산
            val progress = (totalScrolledPixels / scrollableRange * 100).toInt()

            progress.coerceIn(0, 100)
        }
            .distinctUntilChanged()
            .collect { currentPct ->
                if (currentPct > maxPct) {
                    maxPct = currentPct
                }
            }
    }

    DisposableEffect(cafeId) {
        onDispose {
            val dwell = SystemClock.elapsedRealtime() - sessionStart
            if (cafeId < 0 || dwell < 1000) return@onDispose
            viewModel.logViewEnd(cafeId, dwell, maxPct)
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (uiState.cafeDetail != null && uiState.userRole != UserRole.CAFE_OWNER) {
                YeobaegButton(
                    text = "문의",
                    onClick = { /* TODO: 채팅 하기 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error ?: "오류가 발생했습니다",
                        onRetry = { viewModel.loadCafeDetail(cafeId) }
                    )
                }
                uiState.cafeDetail != null -> {
                    CafeDetailContent(
                        cafeDetail = uiState.cafeDetail!!,
                        selectedTab = uiState.selectedTab,
                        onTabSelected = viewModel::selectTab,
                        onBookmarkClick = viewModel::toggleBookmark,
                        onNavigateBack = viewModel::navigateBack,
                        onSlotClick = { slot ->
                            viewModel.navigateToExhibitionCalendar(slot)
                        },
                        bottomPadding = paddingValues.calculateBottomPadding(),
                        listState = listState
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetry) {
            Text("다시 시도")
        }
    }
}

@Composable
fun CafeDetailContent(
    cafeDetail: com.ssafy.yeobaeg.domain.model.cafe.CafeDetail,
    selectedTab: CafeDetailTab,
    onTabSelected: (CafeDetailTab) -> Unit,
    onBookmarkClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onSlotClick: (DisplaySlot) -> Unit = { },
    bottomPadding: androidx.compose.ui.unit.Dp,
    listState: LazyListState = rememberLazyListState()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = bottomPadding),
            state = listState
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    CafeImageGallery(images = cafeDetail.images)
                }
            }

            item {
                CafeBasicInfo(
                    cafeName = cafeDetail.cafeName,
                    categories = cafeDetail.categories,
                    isBookmarked = cafeDetail.isBookmarked,
                    onBookmarkClick = onBookmarkClick
                )
            }

            item {
                CafeDetailTabs(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected
                )
            }

            when (selectedTab) {
                CafeDetailTab.EXHIBITION -> {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        FloorPlanSection(floorPlanSlots = cafeDetail.displaySlots)
                    }

                    item {
                        ExhibitionSlotSectionHeader(slotCount = cafeDetail.displaySlots.size)
                    }

                    if (cafeDetail.displaySlots.isEmpty()) {
                        item {
                            ExhibitionSlotEmptyState()
                        }
                    } else {
                        items(
                            items = cafeDetail.displaySlots.filter { it.status != SlotStatus.UNAVAILABLE },
                            key = { it.wid }
                        ) { slot ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                DisplaySlotCard(
                                    slot = slot,
                                    onClick = {
                                        onSlotClick(slot)
                                    }
                                )
                            }
                        }
                    }
                }
                CafeDetailTab.INFO -> {
                    item {
                        InfoTabContent(cafeDetail = cafeDetail)
                    }
                }
            }
        }
    }
}
