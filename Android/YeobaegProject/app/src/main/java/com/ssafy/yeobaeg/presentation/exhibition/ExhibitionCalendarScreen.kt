package com.ssafy.yeobaeg.presentation.exhibition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.yeobaeg.presentation.exhibition.components.ExhibitionListDialog
import com.ssafy.yeobaeg.presentation.exhibition.components.MonthCalendar
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExhibitionCalendarScreen(
    slotId: Long,
    slotName: String = "전시 슬롯",
    width: Float = 0f,
    height: Float = 0f,
    commission: Float = 0f,
    requirements: String = "",
    viewModel: ExhibitionCalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(slotId) {
        Log.d("ExhibitionCalendarScreen", "Received params: slotId=$slotId, slotName=$slotName, width=$width, height=$height, commission=$commission, requirements=$requirements")
        viewModel.loadSchedule(slotId)
    }

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "$slotName 전시 일정",
                onBackClick = { viewModel.navigateBack() }
            )
        },
        containerColor = YeobaegWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(YeobaegWhite)
        ) {
            // 슬롯 정보 카드
            SlotInfoCard(
                slotName = slotName,
                width = width,
                height = height,
                commission = commission,
                requirements = requirements,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // 메인 컨텐츠
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
                            onRetry = { viewModel.loadSchedule(slotId) },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        if (uiState.schedules.isEmpty()) {
                            EmptyScheduleContent()
                        } else {
                            CalendarContent(
                                schedules = uiState.schedules,
                                startDate = uiState.startDate,
                                selectedDate = uiState.selectedDate,
                                onDateSelected = viewModel::selectDate
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
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
private fun EmptyScheduleContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "예정된 전시가 없습니다",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "이 슬롯에는 아직 전시 일정이 없습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarContent(
    schedules: List<ExhibitionSchedule>,
    startDate: LocalDate,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    // 다이얼로그 표시 여부
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 30일간의 날짜 생성
        val dates = (0 until 30).map { startDate.plusDays(it.toLong()) }

        // 월별로 그룹화
        val monthGroups = dates.groupBy { YearMonth.from(it) }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            monthGroups.forEach { (yearMonth, datesInMonth) ->
                item {
                    MonthCalendar(
                        yearMonth = yearMonth,
                        dates = datesInMonth,
                        schedules = schedules,
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            onDateSelected(date)
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    // 날짜 선택 시 다이얼로그 표시
    if (showDialog && selectedDate != null) {
        ExhibitionListDialog(
            selectedDate = selectedDate,
            schedules = schedules,
            onDismiss = {
                showDialog = false
            }
        )
    }
}

// ============ Previews ============

@Preview(showBackground = true)
@Composable
private fun PreviewEmptyScheduleContent() {
    MaterialTheme {
        EmptyScheduleContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewErrorContent() {
    MaterialTheme {
        ErrorContent(
            error = "일정을 불러올 수 없습니다",
            onRetry = {}
        )
    }
}

@Composable
private fun SlotInfoCard(
    slotName: String,
    width: Float,
    height: Float,
    commission: Float,
    requirements: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "슬롯 정보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = YeobaegDarkBrown
            )

            Divider(color = Color(0xFFEEEEEE))

            // 슬롯 크기
            InfoRow(
                label = "슬롯 크기",
                value = "${width.toInt()}cm × ${height.toInt()}cm"
            )

            // 수수료
            InfoRow(
                label = "수수료",
                value = "${commission.toInt()}%"
            )

            // 요구사항
            if (requirements.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "요구사항",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = requirements,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF212121)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF757575)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = YeobaegDarkBrown
        )
    }
}
