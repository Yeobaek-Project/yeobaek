package com.ssafy.yeobaeg.presentation.home.artist.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.util.clearFocusOnTap
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ArtworkDescriptionContent(
    description: String,
    onDescriptionChange: (String) -> Unit,
    onRegister: () -> Unit,
    // 요약 카드용 데이터
    title: String,
    categoryLabel: String,
    priceText: String,
    sizeText: String,
    weightText: String,
    yearText: String,
    imageCount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp).clearFocusOnTap()) {
        Text("작품 설명", fontFamily = jansilFont, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 500) onDescriptionChange(it) },
            placeholder = {
                Text(
                    text = "작품의 컨셉, 제작 배경, 의미 등을 자유롭게 작성해주세요.",
                    color = YeobaegTextGray,
                    fontFamily = jansilFont,
                    fontSize = 13.sp
                )
            },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0E0E0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        Spacer(Modifier.height(8.dp))
        Text("${description.length}/500", fontFamily = jansilFont, fontSize = 12.sp, color = YeobaegTextGray)

        Spacer(Modifier.height(24.dp))

        // 간단 요약 카드 (뷰모델 의존 제거)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F1E7)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("등록 정보 확인", fontSize = 16.sp, color = Color(0xFF5C3A1E), fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                SummaryRow("작품 제목", title)
                SummaryRow("카테고리", categoryLabel)
                SummaryRow("가격", priceText)
                SummaryRow("크기", sizeText)
                SummaryRow("무게", weightText)
                SummaryRow("제작 연도", yearText)
                SummaryRow("이미지", "${imageCount}장")
            }
        }

        Spacer(Modifier.weight(1f))
        YeobaegButton(
            text = "작품 등록",
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth(),
            enabled = description.isNotBlank()
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF5C3A1E))
        Text(value, color = Color.Black)
    }
}
