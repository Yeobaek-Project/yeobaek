package com.ssafy.yeobaeg.presentation.cafe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeDetailScreen(
    cafeId: Long,
    viewModel: CafeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(cafeId) {
        viewModel.loadCafeDetail(cafeId)
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
                        bottomPadding = paddingValues.calculateBottomPadding()
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
    bottomPadding: androidx.compose.ui.unit.Dp
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = bottomPadding)
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
