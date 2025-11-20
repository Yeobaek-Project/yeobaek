package com.ssafy.yeobaeg.presentation.exhibition.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.model.exhibition.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ExhibitionCard(exhibition: ExhibitionSchedule) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exhibition.artwork.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            ExhibitionStateBadge(state = exhibition.exhibitionState)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = exhibition.artwork.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categoryLabel = ArtworkCategory.values()
                .find { it.name == exhibition.artwork.artCategory }?.label
                ?: exhibition.artwork.artCategory

            Text(
                text = "카테고리: $categoryLabel",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        val startDate = LocalDate.parse(exhibition.startDate)
        val endDate = LocalDate.parse(exhibition.endDate)
        Text(
            text = "전시 기간: ${startDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))} ~ ${endDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExhibitionCard() {
    MaterialTheme {
        ExhibitionCard(
            exhibition = ExhibitionSchedule(
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
            )
        )
    }
}
