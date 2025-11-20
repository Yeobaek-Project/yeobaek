@file:OptIn(ExperimentalMaterial3Api::class)

package com.ssafy.yeobaeg.presentation.home.cafeowner

import android.R.attr.fontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.Exhibition
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray

@Composable
fun SlotDetailSheet(
    slot: DisplaySlot,
    visible: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit = {},
    onClickClose: () -> Unit = {},
    onClickSetting: () -> Unit = {},
) {
    if (!visible) return
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 0.dp
    ) {
        SlotDetailContent(
            slot = slot,
            onDismissRequest = onDismissRequest,
            onClickClose = onClickClose,
            onClickSetting = onClickSetting
        )
    }
}

@Composable
private fun SlotDetailContent(
    slot: DisplaySlot,
    onDismissRequest: () -> Unit,
    onClickClose: () -> Unit,
    onClickSetting: () -> Unit
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${slot.wName} 상세 정보",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismissRequest) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(YeobaegLightGray.copy(alpha = 0.35f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("크기", color = Color(0xFF7A7F87), fontSize = 12.sp)
                    Text(
                        "${slot.width.toInt()}cm × ${slot.height.toInt()}cm",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                StatusChip(status = slot.status)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("수수료", color = Color(0xFF7A7F87), fontSize = 12.sp)
                    Text(
                        "${slot.commission.toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("추가 요청사항", color = Color(0xFF7A7F87), fontSize = 12.sp)
                    Text(
                        slot.requirements,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        val exhibition = slot.exhibition
        if (exhibition != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(YeobaegLightBrown.copy(alpha = 0.12f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("현재 전시 중", color = YeobaegBrown, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(exhibition.artTitle, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("작가: ${exhibition.artist}", color = YeobaegTextGray)
                Text("종료일: ${exhibition.endDate}", color = YeobaegTextGray)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            YeobaegOutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                text = "닫기",
                onClick = onClickClose
            )
            YeobaegButton(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                text = "슬롯 설정",
                onClick = onClickSetting
            )
        }
    }
}

// ============ 프리뷰 ============

@Preview(showBackground = true, name = "슬롯 상세 - 전시 가능")
@Composable
private fun PreviewSlotDetailContent_Available() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            SlotDetailContent(
                slot = DisplaySlot(
                    wid = 1L,
                    wName = "입구 좌측 벽",
                    width = 120f,
                    height = 80f,
                    status = SlotStatus.AVAILABLE,
                    commission = 15f,
                    requirements = "실내 조명 밝음"
                ),
                onDismissRequest = {},
                onClickClose = {},
                onClickSetting = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "슬롯 상세 - 전시 중")
@Composable
private fun PreviewSlotDetailContent_Ongoing() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            SlotDetailContent(
                slot = DisplaySlot(
                    wid = 2L,
                    wName = "메인 전시 공간",
                    width = 1.5f,
                    height = 1.0f,
                    status = SlotStatus.EXHIBITING,
                    commission = 20f,
                    requirements = "대형 작품 가능",
                    exhibition = Exhibition(
                        startDate = "2024-01-01",
                        endDate = "2024-12-31",
                        artId = 101L,
                        artTitle = "푸른 밤의 꿈",
                        artist = "김예술",
                        artImgUrl = "https://picsum.photos/seed/art1/400"
                    )
                ),
                onDismissRequest = {},
                onClickClose = {},
                onClickSetting = {}
            )
        }
    }
}