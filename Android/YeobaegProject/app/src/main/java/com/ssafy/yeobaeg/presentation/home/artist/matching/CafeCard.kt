// CafeCard.kt
package com.ssafy.yeobaeg.presentation.home.artist.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray

// ---------- 색상 (프로젝트 테마에 맞게 조절) ----------
private val PrimaryAccent = Color(0xFFDFB14A)  // 별/포인트 색

// ---------- Composable: CafeCard ----------
@Composable
fun CafeCard(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    title: String,
    address: String,
    rating: Double,
    tags: List<Pair<String, Boolean>> = emptyList(),
    onClick: () -> Unit = {},
    onTagClick: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.White)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 이미지 (rounded top corners)
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                AsyncImage(
                    model = imageUrl ?: "",
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )

                // (선택) 이미지 위에 그라데이션/어두운 오버레이를 더할 수 있음
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x40000000))
                )
            }

            // 하단 컨텐츠
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
//                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = address,
                            fontSize = 14.sp,
                            color = YeobaegBrown,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // 평점 영역
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 별 아이콘 대체로 텍스트 + 색 사용 (원한다면 Icon으로 변경)
                        Text(
                            text = "★",
                            fontSize = 18.sp,
                            color = PrimaryAccent,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = String.format("%.1f", rating),
                            fontSize = 18.sp,
//                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 태그(칩) 그룹
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        val (label, enabled) = tag
                        Surface(
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = if (enabled) YeobaegBrown else YeobaegGray,
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp)
                                    .clickable(enabled = enabled) { if (enabled) onTagClick(label) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    color = if (enabled) Color.White else YeobaegLightGray,
                                    fontWeight = if (enabled) FontWeight.Medium else FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- Preview (샘플 데이터) ----------
@Preview(showBackground = true)
@Composable
private fun CafeCardPreview() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFF121212))
    ) {
        CafeCard(
            modifier = Modifier.fillMaxWidth(),
            imageUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=1200",
            title = "오브젝트 성수점",
            address = "서울시 성동구 연무장길 13",
            rating = 4.8,
            tags = listOf(
                "벽면 1" to true,
                "벽면 2" to true,
                "전시 불가능" to false
            )
        )
    }
}