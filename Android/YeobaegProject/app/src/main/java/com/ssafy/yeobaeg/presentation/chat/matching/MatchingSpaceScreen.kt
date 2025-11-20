package com.ssafy.yeobaeg.presentation.chat.matching

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.domain.model.exhibition.PossibleSlotSummary
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegStepBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

private const val TAG = "MatchingSpaceScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingSpaceScreen(
    viewModel: MatchingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedId = remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(uiState.proposal.startDate, uiState.proposal.endDate) {
        val startDate = uiState.proposal.startDate
        val endDate = uiState.proposal.endDate

        Log.d(TAG, "MatchingSpaceScreen: $startDate $endDate")
        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            viewModel.loadPossibleSlots()
        }
    }


    Scaffold(
        topBar = { YeobaegStepBar(2, 5) },
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(YeobaegWhite)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                YeobaegButton(
                    text = "다음",
                    onClick = {
                        selectedId.value?.let {
                            viewModel.setSpace(it)
                        }
                    },
                    enabled = selectedId.value != null && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { inner ->
        MatchingSpaceContent(
            modifier = Modifier.padding(inner),
            spaceList = uiState.possibleSlots,
            selectedId = selectedId.value,
            onSelect = { selectedId.value = it }
        )
    }
}

@Composable
fun MatchingSpaceContent(
    modifier: Modifier = Modifier,
    spaceList: List<PossibleSlotSummary>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YeobaegWhite)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = "전시 가능한 공간을 선택해주세요.",
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = jansilFont
        )
        Spacer(Modifier.height(20.dp))

        if (spaceList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("사용 가능한 공간이 없습니다.", color = YeobaegTextGray, fontFamily = jansilFont)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(spaceList) { space ->
                    SpaceCard(
                        space = space,
                        selected = space.wallId == selectedId,
                        onClick = { onSelect(space.wallId) }
                    )
                }
            }
        }
    }
}

@Composable
fun SpaceCard(space: PossibleSlotSummary, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, if (selected) YeobaegBrown else YeobaegGray),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                space.wallName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontFamily = jansilFont
            )
            Spacer(Modifier.height(4.dp))
            InfoRow("크기", "${space.width}cm x ${space.height}cm")
            InfoRow("수수료", "${space.commissionRate}%")
            if (space.requirements != null) {
                InfoRow("요구사항", space.requirements)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = YeobaegTextGray,
            fontSize = 13.sp,
            fontFamily = jansilFont
        )
        Text(
            text = value,
            color = Color.Black,
            fontSize = 13.sp,
            fontFamily = jansilFont
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchingSpacePreview() {
    MatchingSpaceContent(
        spaceList = listOf(
            PossibleSlotSummary(
                wallId = 1L,
                wallName = "카페 엔드",
                width = 250.0,
                height = 180.0,
                commissionRate = 10.0,
                requirements = "벽면 설치만 가능"
            ),
            PossibleSlotSummary(
                wallId = 2L,
                wallName = "갤러리 네모",
                width = 300.0,
                height = 200.0,
                commissionRate = 15.0,
                requirements = "3m 이하 작품만 설치 가능"
            )
        ),
        selectedId = 1,
        onSelect = {}
    )
}
