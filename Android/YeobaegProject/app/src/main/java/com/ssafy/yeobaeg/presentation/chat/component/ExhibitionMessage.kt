package com.ssafy.yeobaeg.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.exhibition.ArtworkSummaryInfo
import com.ssafy.yeobaeg.domain.model.exhibition.CafeSummaryInfo
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionInfo
import com.ssafy.yeobaeg.domain.model.exhibition.SlotSummaryInfo
import com.ssafy.yeobaeg.ui.theme.*

@Composable
fun ExhibitionMessage(
    type: String,
    info: ExhibitionInfo,
    onReviewClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ){
        Surface(
            modifier = Modifier
                .width(350.dp),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 1.dp,
            color = Color.White
        ) {
            Column {

                // 상단 바
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD2C1A5))
                        .padding(15.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_end),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (type) {
                                "STARTED" -> "전시 시작"
                                "ENDED" -> "전시 종료"
                                else -> ""
                            },
                            fontFamily = jansilFont,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 내용
                Column(
                    modifier = Modifier.padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = null,
                            tint = YeobaegBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "작가: ${info.artwork.artistName}",
                            fontFamily = jansilFont,
                            fontSize = 16.sp,
                            color = YeobaegBlack
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cafe),
                            contentDescription = null,
                            tint = YeobaegBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "카페: ${info.cafe.cafeName}",
                            fontFamily = jansilFont,
                            fontSize = 16.sp,
                            color = YeobaegBlack
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pin),
                            contentDescription = null,
                            tint = YeobaegBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "전시 공간: ${info.slot.wallName}",
                            fontFamily = jansilFont,
                            fontSize = 16.sp,
                            color = YeobaegBlack
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cal),
                            contentDescription = null,
                            tint = YeobaegBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "기간: ${info.startDate} - ${info.endDate}",
                            fontFamily = jansilFont,
                            fontSize = 16.sp,
                            color = YeobaegBlack
                        )
                    }

                    // 리뷰 버튼 (ENDED일 때만)
                    if (type == "ENDED") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF6A4E2F))
                                .clickable { onReviewClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_star),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(Modifier.width(6.dp))

                                Text(
                                    text = "리뷰하기",
                                    fontFamily = jansilFont,
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Text(
                        text = when (type) {
                            "STARTED" -> "전시가 시작되었습니다! ✨"
                            "ENDED" -> "전시가 성공적으로 종료되었습니다.!"
                            else -> ""
                        },
                        fontFamily = jansilFont,
                        fontSize = 14.sp,
                        color = YeobaegTextGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExhibitionMessageStarted() {

    val dummyInfo = ExhibitionInfo(
        exhibitionId = 1L,
        cafe = CafeSummaryInfo(
            cafeId = 10L,
            cafeName = "너와숲"
        ),
        artwork = ArtworkSummaryInfo(
            artworkId = 20L,
            artistName = "홍길동",
            title = "고요한 풍경"
        ),
        slot = SlotSummaryInfo(
            slotId = 30L,
            wallName = "안쪽 공간 벽 2"
        ),
        startDate = "2025-11-05",
        endDate = "2025-11-30",
        commissionRate = 15.0,
        requirements = "모던 아트 중심"
    )

    ExhibitionMessage(
        type = "STARTED",
        info = dummyInfo,
        onReviewClick = {}
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewExhibitionMessageEnded() {

    val dummyInfo = ExhibitionInfo(
        exhibitionId = 1L,
        cafe = CafeSummaryInfo(
            cafeId = 10L,
            cafeName = "너와숲"
        ),
        artwork = ArtworkSummaryInfo(
            artworkId = 20L,
            artistName = "홍길동",
            title = "고요한 풍경"
        ),
        slot = SlotSummaryInfo(
            slotId = 30L,
            wallName = "안쪽 공간 벽 2"
        ),
        startDate = "2025-11-05",
        endDate = "2025-11-30",
        commissionRate = 15.0,
        requirements = "모던 아트 중심"
    )

    ExhibitionMessage(
        type = "ENDED",
        info = dummyInfo,
        onReviewClick = {}
    )
}
