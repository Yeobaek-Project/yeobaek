package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.util.noRippleClickable
import com.ssafy.yeobaeg.ui.theme.*

@Composable
fun CafeOwnerHomeScreen(
    viewModel: CafeOwnerHomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMyCafe()
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = YeobaegBrown
            )
        }
        return
    }

    val cafeOwnerInfo = state.cafeOwnerInfo
    val selectedTab = state.selectedTab

    val colors = if (cafeOwnerInfo != null) RegisteredColors else UnregisteredColors
    CompositionLocalProvider(LocalCafeOwnerColors provides colors) {
        CafeOwnerHomeScreen(
            cafeOwnerInfo = cafeOwnerInfo,
            selectedTab = selectedTab,
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
}

@Composable
private fun CafeOwnerHomeScreen(
    cafeOwnerInfo: CafeOwnerInfo?,
    selectedTab: CafeOwnerTab = CafeOwnerTab.PLAN,
    onRegisterClick: () -> Unit = {},
    onTabSelected: (CafeOwnerTab) -> Unit = {},
    onScanClick: () -> Unit = {},
    onExhibitionItemClick: (com.ssafy.yeobaeg.domain.model.DisplaySlot) -> Unit = { },
    onMatchingClick: () -> Unit = {},
    onWallClick: (DisplaySlot) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        YeobaegTopAppBar(
            title = "카페 관리",
            onBackClick = {},
            showBackButton = false,
        )
        Spacer(Modifier.height(4.dp))

        if (cafeOwnerInfo == null) {
            CafeOwnerHomeHeader(
                cafeImgUrl = R.drawable.ic_cafe,
                cafeName = "카페를 등록해 주세요.",
                cafeAddress = "-",
                displaySpace = "-",
                display = "-",
                collaboratingArtist = "-"
            )
            Spacer(Modifier.height(8.dp))

            CafeOwnerHomeActions(onRegisterClick = onRegisterClick)

            CafeOwnerHomeEmptyContent()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CafeOwnerHomeHeader(
                    cafeImgUrl = cafeOwnerInfo.cafeImageUrl,
                    cafeName = cafeOwnerInfo.cafeName,
                    cafeAddress = cafeOwnerInfo.cafeAddress,
                    displaySpace = cafeOwnerInfo.numExhibitionSpaces.toString(),
                    display = cafeOwnerInfo.numExhibitedWorks.toString(),
                    collaboratingArtist = cafeOwnerInfo.numArtistsWorkedWith.toString()
                )
                Spacer(Modifier.height(8.dp))

                CafeOwnerHomeActions(
                    onScanClick = onScanClick,
                    onMatchingClick = onMatchingClick
                )
                Spacer(Modifier.height(8.dp))

                CafeOwnerHomeContent(
                    slots = cafeOwnerInfo.displaySlots,
                    tab = selectedTab,
                    onTabSelected = onTabSelected,
                    onWallClick = onWallClick,
                    onExhibitionItemClick = onExhibitionItemClick
                )
            }
        }
    }
}

@Composable
fun CafeOwnerHomeHeader(
    cafeImgUrl: Any,
    cafeName: String,
    cafeAddress: String,
    displaySpace: String,
    display: String,
    collaboratingArtist: String
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = cafeImgUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
    Spacer(Modifier.height(4.dp))
    CafeTitle(cafeName = cafeName, cafeAddress = cafeAddress)
    Spacer(Modifier.height(4.dp))
    CafeDisplayStatus(
        displaySpace = displaySpace,
        display = display,
        collaboratingArtist = collaboratingArtist
    )
}

@Composable
fun CafeOwnerHomeActions(
    onRegisterClick: (() -> Unit)? = null,
    onScanClick: () -> Unit = {},
    onMatchingClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        if (onRegisterClick != null) YeobaegOutlinedButton(
            modifier = Modifier.weight(1f),
            text = "카페 등록",
            icon = R.drawable.ic_cafe_add,
            onClick = onRegisterClick
        )
        else {
            YeobaegOutlinedButton(
                modifier = Modifier.weight(1f),
                text = "공간 스캔",
                icon = R.drawable.ic_cafe_add,
                onClick = onScanClick
            )
            Spacer(Modifier.width(16.dp))

            YeobaegOutlinedButton(
                modifier = Modifier.weight(1f),
                text = "작품 매칭",
                icon = R.drawable.ic_cafe_add,
                onClick = onMatchingClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeOwnerHomeContent(
    slots: List<DisplaySlot>,
    tab: CafeOwnerTab,
    onTabSelected: (CafeOwnerTab) -> Unit = { },
    onWallClick: (DisplaySlot) -> Unit,
    onExhibitionItemClick: (com.ssafy.yeobaeg.domain.model.DisplaySlot) -> Unit = { }
) {
    var sheetVisible by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<DisplaySlot?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        TabRow(
            selectedTabIndex = tab.ordinal,
            containerColor = Color.Transparent,
            indicator = {},
            divider = {}
        ) {
            CafeOwnerTab.entries.forEachIndexed { index, t ->
                val selectedTab = tab.ordinal == index
                Tab(
                    selected = selectedTab,
                    onClick = { onTabSelected(t) },
                    text = {
                        Text(
                            text = t.label,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab) Color.White else YeobaegDarkGray
                        )
                    },
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedTab) YeobaegBrown else YeobaegLightGray)
                        .padding(horizontal = 12.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

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
fun FloorPlanSection(
    slots: List<DisplaySlot>,
    onRowClick: (DisplaySlot) -> Unit
) {
    Spacer(Modifier.height(8.dp))
    FloorPlanPreview(
        initialWalls = slots,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        readOnly = true
    )

    Spacer(Modifier.height(16.dp))
    SlotAvailabilityList(
        slots = slots.filter { it.status != SlotStatus.UNAVAILABLE },
        onRowClick = onRowClick
    )
}

@Composable
private fun SlotAvailabilityList(
    slots: List<DisplaySlot>,
    onRowClick: (DisplaySlot) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        slots.forEach { SlotAvailabilityRow(slot = it, onRowClick = onRowClick) }
    }
}

@Composable
private fun SlotAvailabilityRow(
    slot: DisplaySlot,
    onRowClick: (DisplaySlot) -> Unit
) {
    val dot = when (slot.status) {
        SlotStatus.AVAILABLE -> green
        SlotStatus.EXHIBITING -> blue
        else -> YeobaegLightGray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, YeobaegLightGray.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .noRippleClickable { onRowClick(slot) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dot)
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                slot.wName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(Modifier.height(2.dp))
            Text(slot.sizeText, fontSize = 13.sp, color = YeobaegGray)
        }
        StatusChip(slot.status)
    }
}

@Composable
fun ExhibitionStatusSection(
    slots: List<DisplaySlot>,
    onSlotClick: (com.ssafy.yeobaeg.domain.model.DisplaySlot) -> Unit = { }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DisplaySlotList(
            slots = slots,
            onSlotClick = onSlotClick
        )

        if (slots.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Text("전시 슬롯 데이터가 없습니다.", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun DisplaySlotList(
    slots: List<DisplaySlot>,
    onSlotClick: (com.ssafy.yeobaeg.domain.model.DisplaySlot) -> Unit = { }
) {
    slots.forEach { displaySlot ->
        ExhibitionListItem(
            displaySlot = displaySlot,
            onClick = {
                onSlotClick(displaySlot)
            }
        )
    }
}

@Composable
fun CafeOwnerHomeEmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(color = Color(0xFFFFFBEB), shape = RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "아직 등록한 카페가 없습니다.",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "카페를 등록하고 작가들과 함께\n특별한 전시 공간을 만들어보세요",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CafeOwnerHomeInitScreenPreview() {
    YeobaegProjectTheme {
        CafeOwnerHomeScreen(null)
    }
}

@Preview(showBackground = true)
@Composable
fun CafeOwnerHomeScreenPreview() {
    YeobaegProjectTheme {
        CafeOwnerHomeScreen(
            CafeOwnerInfo(
                cafeImageUrl = "R.drawable.ic_cafe",
                cafeName = "예쁜 카페",
                cafeAddress = "서울특별시 강남구 테헤란로 427",
                numExhibitionSpaces = 3,
                numExhibitedWorks = 15,
                numArtistsWorkedWith = 5
            )
        )
    }
}

@Preview(showBackground = true, name = "카페 등록 완료")
@Composable
private fun PreviewCafeOwnerHomeScreen_Registered_Plan() {
    YeobaegProjectTheme {
        CafeOwnerHomeScreen(
            cafeOwnerInfo = CafeOwnerInfo(
                cafeName = "아트카페 여백",
                cafeAddress = "서울시 강남구 테헤란로 123",
                cafeImageUrl = "",
                numExhibitionSpaces = 5,
                numExhibitedWorks = 12,
                numArtistsWorkedWith = 8
            ),
            selectedTab = CafeOwnerTab.PLAN,
        )
    }
}

@Preview(showBackground = true, name = "카페 등록 완료")
@Composable
private fun PreviewCafeOwnerHomeScreen_Registered_Status() {
    YeobaegProjectTheme {
        CafeOwnerHomeScreen(
            cafeOwnerInfo = CafeOwnerInfo(
                cafeName = "아트카페 여백",
                cafeAddress = "서울시 강남구 테헤란로 123",
                cafeImageUrl = "",
                numExhibitionSpaces = 5,
                numExhibitedWorks = 12,
                numArtistsWorkedWith = 8
            ),
            selectedTab = CafeOwnerTab.STATUS
        )
    }
}

@Preview(showBackground = true, name = "슬롯 리스트")
@Composable
private fun PreviewSlotAvailabilityList() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            SlotAvailabilityList(
                slots = listOf(
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
                            artImgUrl = ""
                        )
                    )
                ),
                onRowClick = {}
            )
        }
    }
}