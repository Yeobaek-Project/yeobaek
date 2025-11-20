package com.ssafy.yeobaeg.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegProjectTheme
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val image: Int
)

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "커피 향 속에서 만나는 예술 한 점",
            description = "바쁜 일상 곁에 잠시 쉬어 갈만한,\n작은 카페 거리를 걸으며 예솔 작품도 보실만해요.",
            image = R.drawable.ic_launcher_foreground
        ),
        OnboardingPage(
            title = "빈 벽을 전시 공간으로",
            description = "남는 벽면을 작가의 작품으로 채워보세요.",
            image = R.drawable.ic_launcher_foreground
        ),
        OnboardingPage(
            title = "작가 전시 지원",
            description = "신진 작가들의 전시 기회를,\n작품하는 예술을 함께 도와줍니다.",
            image = R.drawable.ic_launcher_foreground
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 건너뛰기 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = "건너뛰기",
                        color = YeobaegTextGray,
                        fontSize = 14.sp
                    )
                }
            }

            // HorizontalPager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            // 페이지 인디케이터
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { index ->
                    val color = if (pagerState.currentPage == index) {
                        YeobaegDarkBrown
                    } else {
                        Color.LightGray
                    }
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // 다음 버튼
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onNavigateToLogin()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YeobaegDarkBrown
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "다음" else "시작하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 이미지
        Image(
            painter = painterResource(id = page.image),
            contentDescription = page.title,
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 제목
        Text(
            text = page.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 설명
        Text(
            text = page.description,
            fontSize = 14.sp,
            color = YeobaegTextGray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    YeobaegProjectTheme {
        OnboardingScreen(
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPageContentPreview() {
    YeobaegProjectTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "커피 향 속에서 만나는 예술 한 점",
                description = "바쁜 일상 곁에 잠시 쉬어 갈만한,\n작은 카페 거리를 걸으며 예솔 작품도 보실만해요.",
                image = R.drawable.ic_launcher_foreground
            )
        )
    }
}

