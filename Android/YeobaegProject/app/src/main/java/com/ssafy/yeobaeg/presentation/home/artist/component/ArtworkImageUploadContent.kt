package com.ssafy.yeobaeg.presentation.home.artist.component

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ArtworkImageUploadContent(
    imageUris: List<Uri>,
    onAddImages: (List<Uri>) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = "작품 이미지 업로드",
            fontFamily = jansilFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "작품의 전체 모습과 디테일을 보여주는 사진을 업로드하세요.",
            fontFamily = jansilFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light
        )

        Spacer(Modifier.height(70.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            ArtworkImageUploader(
                images = imageUris,
                onAddImages = onAddImages,
                onRemoveImage = onRemoveImage
            )
        }

        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("최대 5장까지 업로드 가능합니다.", color = Color.Gray, fontSize = 12.sp)
            Spacer(Modifier.height(20.dp))
            YeobaegButton(
                text = "다음",
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                enabled = imageUris.isNotEmpty()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtworkImageUploadContentPreview() {
    val dummyUris = listOf(
        Uri.parse("https://example.com/sample1.jpg"),
        Uri.parse("https://example.com/sample2.jpg")
    )

    ArtworkImageUploadContent(
        imageUris = dummyUris,
        onAddImages = {},
        onRemoveImage = {},
        onNext = {}
    )
}
