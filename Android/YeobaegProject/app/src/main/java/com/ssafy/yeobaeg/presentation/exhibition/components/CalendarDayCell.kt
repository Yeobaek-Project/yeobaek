package com.ssafy.yeobaeg.presentation.exhibition.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.model.exhibition.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionState
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import java.time.LocalDate

@Composable
fun CalendarDayCell(
    date: LocalDate,
    schedules: List<ExhibitionSchedule>,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    isInRange: Boolean,
    modifier: Modifier = Modifier
) {
    val exhibitionsOnDate = if (isInRange) {
        schedules.filter { schedule ->
            val start = LocalDate.parse(schedule.startDate)
            val end = LocalDate.parse(schedule.endDate)
            !date.isBefore(start) && !date.isAfter(end)
        }
    } else {
        emptyList()
    }

    val hasExhibition = exhibitionsOnDate.isNotEmpty()
    val backgroundColor = when {
        !isInRange -> Color.Transparent
        isSelected -> YeobaegBrown.copy(alpha = 0.2f)
        hasExhibition -> YeobaegBrown.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) YeobaegBrown else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = hasExhibition && isInRange) {
                onDateSelected(date)
            }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (hasExhibition) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    !isInRange -> Color.LightGray.copy(alpha = 0.3f)
                    !hasExhibition -> Color.LightGray
                    else -> Color.Black
                }
            )

            if (hasExhibition) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            when (exhibitionsOnDate.first().exhibitionState) {
                                ExhibitionState.SCHEDULED, ExhibitionState.ONGOING -> YeobaegWhite
                                ExhibitionState.COMPLETED -> Color.Gray
                            }
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCalendarDayCell() {
    MaterialTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // 전시 있는 날
            CalendarDayCell(
                date = LocalDate.now(),
                schedules = listOf(
                    ExhibitionSchedule(
                        exhibitionId = 1L,
                        startDate = LocalDate.now().toString(),
                        endDate = LocalDate.now().plusDays(7).toString(),
                        exhibitionState = ExhibitionState.ONGOING,
                        artwork = ArtworkSummary(
                            artworkId = 1L,
                            title = "샘플 전시",
                            description = "전시 설명",
                            artCategory = ArtworkCategory.LANDSCAPE.name
                        )
                    )
                ),
                isSelected = false,
                onDateSelected = {},
                isInRange = true,
                modifier = Modifier.size(50.dp)
            )
            // 선택된 날
            CalendarDayCell(
                date = LocalDate.now().plusDays(1),
                schedules = listOf(
                    ExhibitionSchedule(
                        exhibitionId = 1L,
                        startDate = LocalDate.now().toString(),
                        endDate = LocalDate.now().plusDays(7).toString(),
                        exhibitionState = ExhibitionState.ONGOING,
                        artwork = ArtworkSummary(
                            artworkId = 1L,
                            title = "샘플 전시",
                            description = "전시 설명",
                            artCategory = ArtworkCategory.LANDSCAPE.name
                        )
                    )
                ),
                isSelected = true,
                onDateSelected = {},
                isInRange = true,
                modifier = Modifier.size(50.dp)
            )
            // 전시 없는 날
            CalendarDayCell(
                date = LocalDate.now().plusDays(2),
                schedules = emptyList(),
                isSelected = false,
                onDateSelected = {},
                isInRange = true,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
