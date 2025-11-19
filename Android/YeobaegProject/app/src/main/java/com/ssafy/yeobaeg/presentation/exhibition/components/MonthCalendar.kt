package com.ssafy.yeobaeg.presentation.exhibition.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.model.exhibition.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionState
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthCalendar(
    yearMonth: YearMonth,
    dates: List<LocalDate>,
    schedules: List<ExhibitionSchedule>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    Column {
        // 월 헤더
        Text(
            text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 요일 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = YeobaegBrown
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 캘린더 그리드
        val firstDayOfMonth = yearMonth.atDay(1)
        // DayOfWeek: MONDAY=1, TUESDAY=2, ..., SUNDAY=7
        // 우리 요일 헤더: 일(0), 월(1), 화(2), ..., 토(6)
        val firstDayOfWeek = if (firstDayOfMonth.dayOfWeek == java.time.DayOfWeek.SUNDAY) {
            0
        } else {
            firstDayOfMonth.dayOfWeek.value
        }
        val daysInMonth = yearMonth.lengthOfMonth()

        // 이번 달의 모든 날짜
        val allDatesInMonth = (1..daysInMonth).map { yearMonth.atDay(it) }

        // 주 단위로 그룹화
        val weeks = mutableListOf<List<LocalDate?>>()
        var currentWeek = MutableList<LocalDate?>(7) { null }

        // 첫 주의 빈 칸 채우기
        var dayIndex = firstDayOfWeek

        allDatesInMonth.forEach { date ->
            currentWeek[dayIndex] = date
            dayIndex++

            if (dayIndex == 7) {
                weeks.add(currentWeek.toList())
                currentWeek = MutableList(7) { null }
                dayIndex = 0
            }
        }

        // 마지막 주 추가
        if (currentWeek.any { it != null }) {
            weeks.add(currentWeek.toList())
        }

        // 주별로 표시
        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { date ->
                    if (date != null) {
                        CalendarDayCell(
                            date = date,
                            schedules = schedules,
                            isSelected = date == selectedDate,
                            onDateSelected = onDateSelected,
                            isInRange = date in dates,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PreviewMonthCalendar() {
    MaterialTheme {
        MonthCalendar(
            yearMonth = YearMonth.now(),
            dates = (0 until 30).map { LocalDate.now().plusDays(it.toLong()) },
            schedules = listOf(
                ExhibitionSchedule(
                    exhibitionId = 1L,
                    startDate = LocalDate.now().toString(),
                    endDate = LocalDate.now().plusDays(7).toString(),
                    exhibitionState = ExhibitionState.ONGOING,
                    artwork = ArtworkSummary(
                        artworkId = 1L,
                        title = "현대미술 전시",
                        description = "전시 설명",
                        artCategory = ArtworkCategory.LANDSCAPE.name
                    )
                )
            ),
            selectedDate = null,
            onDateSelected = {}
        )
    }
}
