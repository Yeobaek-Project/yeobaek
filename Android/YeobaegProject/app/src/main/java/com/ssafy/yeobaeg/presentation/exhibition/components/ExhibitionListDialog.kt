package com.ssafy.yeobaeg.presentation.exhibition.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.model.exhibition.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionState
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegTextPrimary
import com.ssafy.yeobaeg.ui.theme.YeobaegTextSecondary
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExhibitionListDialog(
    selectedDate: LocalDate,
    schedules: List<ExhibitionSchedule>,
    onDismiss: () -> Unit
) {
    val exhibitionsOnDate = schedules.filter { schedule ->
        val start = LocalDate.parse(schedule.startDate)
        val end = LocalDate.parse(schedule.endDate)
        !selectedDate.isBefore(start) && !selectedDate.isAfter(end)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = YeobaegWhite,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 제목
                Text(
                    text = "${selectedDate.format(DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN))} 전시",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = YeobaegTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 전시 목록 또는 빈 상태 메시지
                if (exhibitionsOnDate.isEmpty()) {
                    Text(
                        text = "이 날짜에는 전시가 없습니다",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = YeobaegTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(exhibitionsOnDate) { exhibition ->
                            ExhibitionCard(exhibition = exhibition)
                            if (exhibition != exhibitionsOnDate.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color(0xFFE0E0E0)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 확인 버튼
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YeobaegDarkBrown,
                        contentColor = YeobaegWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "확인",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun PreviewExhibitionListDialog() {
    MaterialTheme {
        ExhibitionListDialog(
            selectedDate = LocalDate.now(),
            schedules = listOf(
                ExhibitionSchedule(
                    exhibitionId = 1L,
                    startDate = LocalDate.now().toString(),
                    endDate = LocalDate.now().plusDays(30).toString(),
                    exhibitionState = ExhibitionState.ONGOING,
                    artwork = ArtworkSummary(
                        artworkId = 1L,
                        title = "현대미술 전시",
                        description = "현대미술의 다양한 작품들을 만나보세요",
                        artCategory = ArtworkCategory.LANDSCAPE.name
                    )
                ),
                ExhibitionSchedule(
                    exhibitionId = 2L,
                    startDate = LocalDate.now().toString(),
                    endDate = LocalDate.now().plusDays(30).toString(),
                    exhibitionState = ExhibitionState.SCHEDULED,
                    artwork = ArtworkSummary(
                        artworkId = 2L,
                        title = "인물화 전시",
                        description = "감동적인 인물화 작품들",
                        artCategory = ArtworkCategory.PORTRAIT.name
                    )
                )
            ),
            onDismiss = {}
        )
    }
}
