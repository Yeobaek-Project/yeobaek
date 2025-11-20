package com.ssafy.yeobaeg.presentation.common.lottie

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.yeobaeg.R
import kotlinx.coroutines.delay

@Composable
fun YeobaegLottieLoading(messages: List<String>) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_recommendations)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(composition, { progress }, Modifier.fillMaxWidth())
        LoadingTextTicker(messages)
    }

}

@Composable
fun LoadingTextTicker(
    messages: List<String>,
    intervalMs: Long = 2000L,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium
) {
    val safeMessages = messages.ifEmpty { listOf("") }

    var index by remember { mutableStateOf(0) }

    // 최신 intervalMs/messages 반영
    val currentInterval by rememberUpdatedState(newValue = intervalMs)
    val currentMessages by rememberUpdatedState(newValue = safeMessages)

    // 인덱스 증가 코루틴 — 컴포저블이 사라지면 자동 취소됨
    LaunchedEffect(currentMessages) {
        // 초기 지연 없이 바로 첫 문구 보여주고 순환
        while (true) {
            delay(currentInterval)
            index = (index + 1) % currentMessages.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(), // 텍스트 높이만큼
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = index) { i ->
            Text(
                text = currentMessages[i],
                style = textStyle,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}