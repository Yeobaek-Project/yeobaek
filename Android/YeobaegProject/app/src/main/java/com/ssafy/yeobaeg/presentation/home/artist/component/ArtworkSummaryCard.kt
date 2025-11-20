package com.ssafy.yeobaeg.presentation.home.artist.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.presentation.common.util.formatPriceInput
import com.ssafy.yeobaeg.presentation.home.artist.ArtworkRegisterViewModel
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ArtworkSummaryCard(viewModel: ArtworkRegisterViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val data = uiState.artworkData

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F1E7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("등록 정보 확인", fontSize = 16.sp, color = YeobaegBrown, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            SummaryRow("작품 제목", data.title)
            SummaryRow("카테고리", data.category.label)
            SummaryRow("가격", if (data.price > 0) "₩${formatPriceInput(data.price.toString())}" else "")
            SummaryRow("크기", "${data.widthCm}cm × ${data.heightCm}cm")
            SummaryRow("무게", "${data.weightKg}kg")
            SummaryRow("제작 연도", "${data.productionYear}년")
            SummaryRow("이미지", "${uiState.imageUris.size}장")
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF5C3A1E))
        Text(value, color = Color.Black)
    }
}

@Preview
@Composable
fun ArtworkSummaryCardPreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = BorderStroke(1.dp, YeobaegBrown)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "등록 정보 확인",
                fontFamily = jansilFont,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C3A1E)
            )
            Spacer(Modifier.height(8.dp))
            SummaryRow("작품 제목", "햇살이 스미는 풍경")
            SummaryRow("카테고리", "풍경화")
            SummaryRow("가격", "₩150,000")
            SummaryRow("크기", "30cm × 40cm")
            SummaryRow("제작연도", "2024")
        }
    }
}
