package com.ssafy.yeobaeg.presentation.home.artist.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ArtworkCategorySelector(
    selectedCategory: ArtworkCategory,
    onSelect: (ArtworkCategory) -> Unit
) {
    // 카테고리 2행 × 3열 형태로 구성
    val categories = listOf(
        listOf(
            ArtworkCategory.LANDSCAPE,
            ArtworkCategory.PORTRAIT,
            ArtworkCategory.STILL_LIFE
        ),
        listOf(
            ArtworkCategory.HISTORY,
            ArtworkCategory.GENRE,
            ArtworkCategory.OTHER
        )
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "카테고리",
            fontFamily = jansilFont,
            fontWeight = FontWeight.Normal
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_essential),
            contentDescription = "essential",
            tint = Color(0xFFFF5A5A),
            modifier = Modifier
                .padding(start = 2.dp)
                .size(10.dp)
        )
    }

    Spacer(Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        categories.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { category ->
                    val isSelected = category == selectedCategory
                    OutlinedButton(
                        onClick = { onSelect(category) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) YeobaegBrown else YeobaegLightGray,
                            contentColor = if (isSelected) Color.White else YeobaegTextGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = category.label,
                            fontFamily = jansilFont,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtworkCategorySelectorPreview() {
    ArtworkCategorySelector(
        selectedCategory = ArtworkCategory.LANDSCAPE,
        onSelect = {}
    )
}
