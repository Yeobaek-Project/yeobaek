package com.ssafy.yeobaeg.presentation.home.artist.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedTextField
import com.ssafy.yeobaeg.presentation.common.util.clearFocusOnTap
import com.ssafy.yeobaeg.presentation.common.util.formatPriceInput
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ArtworkBasicInfoContent(
    title: String,
    category: ArtworkCategory,
    price: String,
    onTitleChange: (String) -> Unit,
    onCategorySelect: (ArtworkCategory) -> Unit,
    onPriceChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .clearFocusOnTap()
    ) {
        Text("기본 정보", fontSize = 18.sp, fontFamily = jansilFont, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(20.dp))

        YeobaegOutlinedTextField(
            title = "작품 제목",
            essential = true,
            value = title,
            onValueChange = onTitleChange,
            placeholder = "제목을 입력하세요",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        ArtworkCategorySelector(
            selectedCategory = category,
            onSelect = onCategorySelect
        )

        Spacer(Modifier.height(12.dp))

        YeobaegOutlinedTextField(
            title = "가격",
            essential = true,
            unit = "원",
            value = price,
            onValueChange = { input -> onPriceChange(formatPriceInput(input)) },
            placeholder = "가격을 입력하세요",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        YeobaegButton(
            text = "다음",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && price.isNotBlank()
        )
    }
}
