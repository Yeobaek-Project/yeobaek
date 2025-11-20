package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.Exhibition
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBlue
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGreen
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkYellow
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightBlue
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGreen
import com.ssafy.yeobaeg.ui.theme.YeobaegLightYellow

/** 상태 칩 */
@Composable
fun StatusChip(status: SlotStatus) {
    val (bg, fg, label) = when (status) {
        SlotStatus.EXHIBITING -> Triple(YeobaegLightGreen, YeobaegDarkGreen, "전시 중")
        SlotStatus.AVAILABLE -> Triple(YeobaegLightBlue, YeobaegDarkBlue, "전시 가능")
        else -> Triple(YeobaegLightGray, YeobaegDarkGray, "전시 불가능")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}


/**
 * 전시 리스트 아이템
 *
 * @param displaySlot DisplaySlot 객체 (전시 공간 정보)
 * @param onClick 카드 클릭 시 콜백
 */
@Composable
fun ExhibitionListItem(
    modifier: Modifier = Modifier,
    displaySlot: DisplaySlot,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE6E6E8)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 썸네일
            AsyncImage(
                model = displaySlot.exhibition?.artImgUrl?.ifEmpty { null },
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(YeobaegLightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                // 상단: 제목 + 상태칩
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displaySlot.wName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.weight(1f))
                    StatusChip(displaySlot.status)
                }
                Spacer(Modifier.height(6.dp))

                when (displaySlot.status) {
                    SlotStatus.EXHIBITING -> {
                        // 작품명 / 작가 / 종료
                        displaySlot.exhibition?.let { exhibition ->
                            Text("🎨 ${exhibition.artTitle}", fontSize = 13.sp, color = YeobaegDarkGray)
                            Text("👩‍🎨 ${exhibition.artist}", fontSize = 13.sp, color = YeobaegDarkGray)
                            Spacer(Modifier.height(2.dp))
                            Text("종료 : ${exhibition.endDate}", fontSize = 12.sp, color = YeobaegGray)
                        }
                    }

                    SlotStatus.AVAILABLE -> {
                        // 크기 + 작품 매칭 버튼
                        Text("📏 ${displaySlot.sizeText}", fontSize = 13.sp, color = YeobaegDarkGray)
                        Spacer(Modifier.height(8.dp))

//                        Button(
//                            onClick = { onMatchClick() },
//                            colors = ButtonDefaults.buttonColors(containerColor = YeobaegDarkBrown),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Text(
//                                text = "작품 매칭",
//                                style = MaterialTheme.typography.bodyLarge,
//                                fontSize = 14.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = Color.White
//                            )
//                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExhibitionListItem_Available() {
    MaterialTheme {
        ExhibitionListItem(
            displaySlot = DisplaySlot(
                wid = 1L,
                wName = "입구 좌측 벽",
                width = 1.2f,
                height = 0.8f,
                status = SlotStatus.AVAILABLE,
                commission = 10f,
                requirements = "",
                exhibition = null
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExhibitionListItem_Ongoing() {
    MaterialTheme {
        ExhibitionListItem(
            displaySlot = DisplaySlot(
                wid = 2L,
                wName = "메인 전시 공간",
                width = 2.0f,
                height = 1.5f,
                status = SlotStatus.EXHIBITING,
                commission = 15f,
                requirements = "",
                exhibition = Exhibition(
                    startDate = "2024-01-01",
                    endDate = "2024-12-31",
                    artId = 100L,
                    artTitle = "푸른 밤의 꿈",
                    artist = "김예술",
                    artImgUrl = ""
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExhibitionListItem_Reserved() {
    MaterialTheme {
        ExhibitionListItem(
            displaySlot = DisplaySlot(
                wid = 3L,
                wName = "창가 벽면",
                width = 1.0f,
                height = 1.0f,
                status = SlotStatus.EXHIBITING,
                commission = 12f,
                requirements = "",
                exhibition = Exhibition(
                    startDate = "2025-01-01",
                    endDate = "2025-01-15",
                    artId = 101L,
                    artTitle = "푸른 밤의 꿈",
                    artist = "김예술",
                    artImgUrl = ""
                )
            )
        )
    }
}