package com.ssafy.yeobaeg.presentation.exhibition.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionState

@Composable
fun ExhibitionStateBadge(state: ExhibitionState) {
    val (text, color) = when (state) {
        ExhibitionState.ONGOING -> "진행중" to Color(0xFF4CAF50)
        ExhibitionState.SCHEDULED -> "예정" to Color(0xFF2196F3)
        ExhibitionState.COMPLETED -> "완료" to Color.Gray
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExhibitionStateBadge() {
    MaterialTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            ExhibitionStateBadge(state = ExhibitionState.ONGOING)
            ExhibitionStateBadge(state = ExhibitionState.SCHEDULED)
            ExhibitionStateBadge(state = ExhibitionState.COMPLETED)
        }
    }
}
