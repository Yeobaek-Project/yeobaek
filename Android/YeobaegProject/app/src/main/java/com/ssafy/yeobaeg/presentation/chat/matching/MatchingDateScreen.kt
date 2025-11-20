package com.ssafy.yeobaeg.presentation.chat.matching

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegStepBar
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "MatchingDateScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingDateScreen(
    myId:Long?,
    myRole: UserRole?,
    partnerId:Long,
    viewModel: MatchingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val datePickerState = rememberDateRangePickerState()
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

//    LaunchedEffect(Unit) {
//        viewModel.setChatContext(myRole, partnerId)
//        viewModel.setMyId(myId)
//    }
    Scaffold(
        topBar = { YeobaegStepBar(1, 5) },
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
                        val start =
                            datePickerState.selectedStartDateMillis?.let { formatter.format(Date(it)) }
                        val end =
                            datePickerState.selectedEndDateMillis?.let { formatter.format(Date(it)) }
                        Log.d(TAG, "MatchingDateScreen: $myId $myRole $partnerId $start $end")

                        if (start != null && end != null) {
                            viewModel.setChatContext(myRole, partnerId)
                            viewModel.setMyId(myId)
                            viewModel.setDate(start, end)
                        }
                    },
                    enabled = !uiState.isLoading && datePickerState.selectedStartDateMillis != null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { inner ->
        MatchingDateContent(Modifier.padding(inner), datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingDateContent(
    modifier: Modifier = Modifier,
    state: DateRangePickerState
) {
    val formatter = remember { SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()) }
    val start = state.selectedStartDateMillis?.let { formatter.format(Date(it)) }
    val end = state.selectedEndDateMillis?.let { formatter.format(Date(it)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YeobaegWhite)
            .padding(horizontal = 24.dp)
    ) {

        Spacer(Modifier.height(6.dp))

        Text(
            "희망 전시 기간을 선택해주세요.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = jansilFont
        )

        Spacer(Modifier.height(16.dp))

        // 날짜 표시 박스
        Surface(
            color = Color(0xFFF6F6F6),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (start != null && end != null)
                        "$start ~ $end"
                    else
                        "날짜를 선택해주세요",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // 달력 UI
        DateRangePicker(
            state = state,
            showModeToggle = false,
            title = null,
            headline = null,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                selectedDayContainerColor = Color(0xFFDCC8A5),
                selectedDayContentColor = Color.Black,
                todayDateBorderColor = Color.Transparent,
                weekdayContentColor = Color(0xFF757575),
                dayInSelectionRangeContainerColor = Color(0xFFF3EEE5)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MatchingDatePreview() {
    MatchingDateContent(state = rememberDateRangePickerState())
}
