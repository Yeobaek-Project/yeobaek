package com.ssafy.yeobaeg.presentation.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = YeobaegDarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Preview(showBackground = true, name = "SectionHeader - Step1")
@Composable
fun Preview_SectionHeader_Step1() {
    MaterialTheme {
        SectionHeader(
            title = "기본 정보",
            subtitle = "카페의 기본 정보를 입력해주세요",
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(showBackground = true, name = "SectionHeader - Step2")
@Composable
fun Preview_SectionHeader_Step2() {
    MaterialTheme {
        SectionHeader(
            title = "매장 특성",
            subtitle = "매장의 특성과 분위기를 알려주세요",
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(showBackground = true, name = "SectionHeader - Long Subtitle")
@Composable
fun Preview_SectionHeader_Long() {
    MaterialTheme {
        SectionHeader(
            title = "상세 안내",
            subtitle = "매장 소개는 최대 500자까지 입력할 수 있으며 핵심 정보를 간결하게 작성해주세요",
            modifier = Modifier.padding(20.dp)
        )
    }
}