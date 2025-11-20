package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.Exhibition
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwnerInfo
import com.ssafy.yeobaeg.presentation.arcore.plan.FloorPlanPreview
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegProjectTheme
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

@Composable
fun CafeOwnerHomeScreen(
    viewModel: CafeOwnerHomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMyCafe()
    }

    CafeOwnerHomeContent(
        state = state,
        onRegisterClick = viewModel::navigateToRegister,
        onTabSelected = { viewModel.selectTab(it) },
        onScanClick = viewModel::navigateToSpaceScan,
        onWallClick = viewModel::navigateToSlot,
        onExhibitionItemClick = { slot ->
            viewModel.navigateToExhibitionCalendar(slot)
        },
        onMatchingClick = viewModel::navigateToMatching,
    )
}

@Composable
fun CafeOwnerHomeContent(
    state: CafeOwnerHomeUiState,
    onRegisterClick: () -> Unit = {},
    onTabSelected: (CafeOwnerTab) -> Unit = {},
    onScanClick: () -> Unit = {},
    onExhibitionItemClick: (DisplaySlot) -> Unit = {},
    onMatchingClick: () -> Unit = {},
    onWallClick: (DisplaySlot) -> Unit = {}
) {
    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = null,
                titleIcon = R.drawable.logo_title,
                onBackClick = {},
                showBackButton = false,
            )
        }
    ) { inner ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.cafeOwnerInfo != null -> {
                CafeOwnerHomeContent(
                    cafeOwnerInfo = state.cafeOwnerInfo,
                    selectedTab = state.selectedTab,
                    innerPadding = inner,
                    onRegisterClick = onRegisterClick,
                    onTabSelected = onTabSelected,
                    onScanClick = onScanClick,
                    onExhibitionItemClick = onExhibitionItemClick,
                    onMatchingClick = onMatchingClick,
                    onWallClick = onWallClick
                )
            }

            else -> {
                // Empty State
                CafeOwnerEmptyContent(
                    innerPadding = inner,
                    onRegisterClick = onRegisterClick
                )
            }
        }
    }
}

@Composable
private fun CafeOwnerEmptyContent(
    innerPadding: PaddingValues,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YeobaegWhite)
            .padding(innerPadding)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CafeOwnerEmptyCard(onRegisterClick = onRegisterClick)
    }
}

@Composable
private fun CafeOwnerHomeContent(
    cafeOwnerInfo: CafeOwnerInfo,
    selectedTab: CafeOwnerTab,
    innerPadding: PaddingValues,
    onRegisterClick: () -> Unit,
    onTabSelected: (CafeOwnerTab) -> Unit,
    onScanClick: () -> Unit,
    onExhibitionItemClick: (DisplaySlot) -> Unit,
    onMatchingClick: () -> Unit,
    onWallClick: (DisplaySlot) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YeobaegWhite)
            .padding(top = innerPadding.calculateTopPadding())
            .verticalScroll(scrollState)
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Profile Card
        CafeProfileCard(cafeOwnerInfo = cafeOwnerInfo)

        Spacer(Modifier.height(16.dp))

        // Action Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard(
                modifier = Modifier.weight(1f),
                icon = R.drawable.space_scan,
                title = "공간 스캔",
                description = "전시 공간을\n측정하세요",
                onClick = onScanClick
            )
            ActionCard(
                modifier = Modifier.weight(1f),
                icon = R.drawable.matching,
                title = "작품 매칭",
                description = "작가와\n매칭하세요",
                onClick = onMatchingClick
            )
        }

        Spacer(Modifier.height(32.dp))

        // Section Title
        Text(
            text = "전시 공간 관리",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Tabs
        CafeOwnerTabs(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )

        Spacer(Modifier.height(16.dp))

        // Content
        CafeOwnerHomeTabContent(
            slots = cafeOwnerInfo.displaySlots,
            tab = selectedTab,
            onWallClick = onWallClick,
            onExhibitionItemClick = onExhibitionItemClick
        )

    }
}

/* ───────────── Profile Card ───────────── */

@Composable
private fun CafeProfileCard(cafeOwnerInfo: CafeOwnerInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cafe Image
            AsyncImage(
                model = cafeOwnerInfo.cafeImageUrl.ifBlank { R.drawable.ic_cafe },
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp,150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(24.dp))

            // Info
            Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                Text(
                    text = cafeOwnerInfo.cafeName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = cafeOwnerInfo.cafeAddress,
                    fontSize = 13.sp,
                    color = Color(0xFF999999),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(16.dp))

                // Stats
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatRow(
                        label = "전시 공간",
                        value = cafeOwnerInfo.numExhibitionSpaces.toString()
                    )
                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    StatRow(
                        label = "전시 중인 작품",
                        value = cafeOwnerInfo.numExhibitedWorks.toString()
                    )
                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    StatRow(
                        label = "협업 작가",
                        value = cafeOwnerInfo.numArtistsWorkedWith.toString()
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )
    }
}

/* ───────────── Empty State ───────────── */

@Composable
private fun CafeOwnerEmptyCard(onRegisterClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "☕",
                fontSize = 60.sp
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "아직 등록한 카페가 없습니다",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "카페를 등록하고 작가들과 함께\n특별한 전시 공간을 만들어보세요",
                fontSize = 14.sp,
                color = Color(0xFF999999),
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B4513)
                )
            ) {
                Text(
                    text = "카페 등록하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* ───────────── Action Cards ───────────── */

@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    icon: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 6.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.size(70.dp),
            )

            Spacer(Modifier.width(8.dp))

            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    lineHeight = 16.sp
                )
            }
            Spacer(Modifier.width(10.dp))
        }
    }
}

/* ───────────── Tabs ───────────── */

@Composable
private fun CafeOwnerTabs(
    selectedTab: CafeOwnerTab,
    onTabSelected: (CafeOwnerTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CafeOwnerTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) Color(0xFF8B4513)
                            else Color.Transparent
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF999999)
                    )
                }
            }
        }
    }
}

/* ───────────── Tab Content ───────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CafeOwnerHomeTabContent(
    slots: List<DisplaySlot>,
    tab: CafeOwnerTab,
    onWallClick: (DisplaySlot) -> Unit,
    onExhibitionItemClick: (DisplaySlot) -> Unit
) {
    var sheetVisible by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<DisplaySlot?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    when (tab) {
        CafeOwnerTab.PLAN -> FloorPlanSection(
            slots = slots,
            onRowClick = {
                selected = it
                sheetVisible = true
            }
        )

        CafeOwnerTab.STATUS -> ExhibitionStatusSection(
            slots = slots.filter { it.status != SlotStatus.UNAVAILABLE },
            onSlotClick = onExhibitionItemClick
        )
    }

    selected?.let { slot ->
        SlotDetailSheet(
            visible = sheetVisible,
            sheetState = sheetState,
            slot = slot,
            onDismissRequest = {
                sheetVisible = false
                selected = null
            },
            onClickClose = {
                sheetVisible = false
                selected = null
            },
            onClickSetting = {
                sheetVisible = false
                selected?.let { onWallClick(it) }
                selected = null
            }
        )
    }
}

@Composable
private fun FloorPlanSection(
    slots: List<DisplaySlot>,
    onRowClick: (DisplaySlot) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        // Floor Plan Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                FloorPlanPreview(
                    initialWalls = slots,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    readOnly = true
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Slot List
        SlotAvailabilityList(
            slots = slots.filter { it.status != SlotStatus.UNAVAILABLE },
            onRowClick = onRowClick
        )
    }
}

@Composable
private fun SlotAvailabilityList(
    slots: List<DisplaySlot>,
    onRowClick: (DisplaySlot) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        slots.forEach {
            SlotAvailabilityRow(slot = it, onRowClick = onRowClick)
        }
    }
}

@Composable
private fun SlotAvailabilityRow(
    slot: DisplaySlot,
    onRowClick: (DisplaySlot) -> Unit
) {
    val dotColor = when (slot.status) {
        SlotStatus.EXHIBITING -> Color(0xFF4CAF50)
        SlotStatus.AVAILABLE -> Color(0xFF2196F3)
        else -> Color(0xFFE0E0E0)
    }

    val badgeColor = when (slot.status) {
        SlotStatus.EXHIBITING -> Color(0xFFE8F5E9) to Color(0xFF4CAF50)
        SlotStatus.AVAILABLE -> Color(0xFFE3F2FD) to Color(0xFF2196F3)
        else -> Color(0xFFF5F5F5) to Color(0xFF999999)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRowClick(slot) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = slot.wName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF222222)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = slot.sizeText,
                    fontSize = 13.sp,
                    color = Color(0xFF999999)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor.first
            ) {
                Text(
                    text = when (slot.status) {
                        SlotStatus.AVAILABLE -> "전시 가능"
                        SlotStatus.EXHIBITING -> "전시 중"
                        else -> "사용 불가"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeColor.second,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/* ───────────── Exhibition Status Section ───────────── */

@Composable
private fun ExhibitionStatusSection(
    slots: List<DisplaySlot>,
    onSlotClick: (DisplaySlot) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (slots.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "전시 슬롯 데이터가 없습니다",
                        color = Color(0xFF999999)
                    )
                }
            }
        } else {
            slots.forEach { displaySlot ->
                ExhibitionListItem(
                    displaySlot = displaySlot,
                    onClick = { onSlotClick(displaySlot) }
                )
            }
        }
    }
}

@Composable
private fun ExhibitionListItem(
    displaySlot: DisplaySlot,
    onClick: () -> Unit
) {
    val badgeColor = when (displaySlot.status) {
        SlotStatus.EXHIBITING -> Color(0xFFE8F5E9) to Color(0xFF4CAF50)
        SlotStatus.AVAILABLE -> Color(0xFFE3F2FD) to Color(0xFF2196F3)
        else -> Color(0xFFF5F5F5) to Color(0xFF999999)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exhibition Image (if available)
            if (displaySlot.exhibition?.artImgUrl?.isNotBlank() == true) {
                AsyncImage(
                    model = displaySlot.exhibition.artImgUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F0F0)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displaySlot.wName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
                Spacer(Modifier.height(4.dp))

                if (displaySlot.exhibition != null) {
                    Text(
                        text = displaySlot.exhibition.artTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = displaySlot.exhibition.artist,
                        fontSize = 13.sp,
                        color = Color(0xFF999999)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${displaySlot.exhibition.startDate} ~ ${displaySlot.exhibition.endDate}",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                } else {
                    Text(
                        text = displaySlot.sizeText,
                        fontSize = 13.sp,
                        color = Color(0xFF999999)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor.first
            ) {
                Text(
                    text = when (displaySlot.status) {
                        SlotStatus.AVAILABLE -> "전시 가능"
                        SlotStatus.EXHIBITING -> "전시 중"
                        else -> "사용 불가"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeColor.second,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/* ───────────── Previews ───────────── */

@Preview(showBackground = true)
@Composable
fun CafeOwnerHomeInitScreenPreview() {
    YeobaegProjectTheme {
        CafeOwnerHomeContent(
            state = CafeOwnerHomeUiState(
                isLoading = false,
                cafeOwnerInfo = null,
                selectedTab = CafeOwnerTab.PLAN
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCafeOwnerHomeScreen_Registered_Plan() {
    YeobaegProjectTheme {
        CafeOwnerHomeContent(
            state = CafeOwnerHomeUiState(
                isLoading = false,
                cafeOwnerInfo = CafeOwnerInfo(
                    cafeName = "아트카페 여백",
                    cafeAddress = "서울시 강남구 테헤란로 123",
                    cafeImageUrl = "",
                    numExhibitionSpaces = 5,
                    numExhibitedWorks = 12,
                    numArtistsWorkedWith = 8,
                    displaySlots = listOf(
                        DisplaySlot(
                            wid = 1L,
                            wName = "입구 좌측 벽",
                            width = 1.2f,
                            height = 0.8f,
                            status = SlotStatus.AVAILABLE,
                            commission = 15f,
                            requirements = "실내 조명 밝음",
                            exhibition = null
                        ),
                        DisplaySlot(
                            wid = 2L,
                            wName = "메인 전시 공간",
                            width = 1.5f,
                            height = 1.0f,
                            status = SlotStatus.EXHIBITING,
                            commission = 15f,
                            requirements = "",
                            exhibition = Exhibition(
                                startDate = "2024-01-01",
                                endDate = "2024-12-31",
                                artId = 100L,
                                artTitle = "푸른 밤의 꿈",
                                artist = "김예술",
                                artImgUrl = ""
                            )
                        )
                    )
                ),
                selectedTab = CafeOwnerTab.PLAN
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCafeOwnerHomeScreen_Registered_Status() {
    YeobaegProjectTheme {
        CafeOwnerHomeContent(
            state = CafeOwnerHomeUiState(
                isLoading = false,
                cafeOwnerInfo = CafeOwnerInfo(
                    cafeName = "아트카페 여백",
                    cafeAddress = "서울시 강남구 테헤란로 123",
                    cafeImageUrl = "",
                    numExhibitionSpaces = 5,
                    numExhibitedWorks = 12,
                    numArtistsWorkedWith = 8,
                    displaySlots = listOf(
                        DisplaySlot(
                            wid = 2L,
                            wName = "메인 전시 공간",
                            width = 1.5f,
                            height = 1.0f,
                            status = SlotStatus.EXHIBITING,
                            commission = 15f,
                            requirements = "",
                            exhibition = Exhibition(
                                startDate = "2024-01-01",
                                endDate = "2024-12-31",
                                artId = 100L,
                                artTitle = "푸른 밤의 꿈",
                                artist = "김예술",
                                artImgUrl = "https://picsum.photos/seed/art1/300/200"
                            )
                        ),
                        DisplaySlot(
                            wid = 3L,
                            wName = "창가 벽면",
                            width = 1.0f,
                            height = 1.0f,
                            status = SlotStatus.EXHIBITING,
                            commission = 15f,
                            requirements = "",
                            exhibition = Exhibition(
                                startDate = "2024-12-01",
                                endDate = "2025-01-15",
                                artId = 101L,
                                artTitle = "겨울 풍경",
                                artist = "박작가",
                                artImgUrl = "https://picsum.photos/seed/art2/300/200"
                            )
                        )
                    )
                ),
                selectedTab = CafeOwnerTab.STATUS
            )
        )
    }
}