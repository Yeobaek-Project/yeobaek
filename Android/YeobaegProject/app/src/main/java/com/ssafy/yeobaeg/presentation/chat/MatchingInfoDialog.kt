package com.ssafy.yeobaeg.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionInfo
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.jansilFont


@Composable
fun MatchingInfoDialog(
    info: ExhibitionInfo,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YeobaegWhite)
                    .padding(8.dp)
            ) {
                Text(
                    text = "전시 공간 제안",
                    fontFamily = jansilFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                InfoCard(
                    title = "제안 전시 공간",
                    name = info.slot.wallName,
                    detail = info.slot.widthCm.toString() + info.slot.heightCm.toString()
                )
                Spacer(Modifier.height(8.dp))
                InfoCard(
                    title = "전시 작품",
                    name = info.artwork.title,
                    detail = info.artwork.widthCm.toString() + info.artwork.heightCm
                )

                Spacer(Modifier.height(12.dp))

                InfoField("희망 전시 기간", "${info.startDate} - ${info.endDate}")
                InfoField("수수료 (%)", info.commissionRate.toString())
                InfoField("추가 조건", info.requirements ?: "없음")

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = YeobaegLightGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("닫기", fontFamily = jansilFont, color = Color.Black)
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = YeobaegBrown),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("수락", fontFamily = jansilFont, color = YeobaegWhite)
                    }
                }
            }
        },
        containerColor = YeobaegWhite,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun InfoCard(title: String, name: String, detail: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, YeobaegLightGray, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontFamily = jansilFont,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = name,
            fontFamily = jansilFont,
            fontSize = 15.sp,
            color = Color.Black
        )
        Text(
            text = detail,
            fontFamily = jansilFont,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun InfoField(label: String, value: String) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            fontFamily = jansilFont,
            fontSize = 13.sp,
            color = Color.Gray
        )
        Spacer(Modifier.height(4.dp))
        Surface(
            color = YeobaegWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, YeobaegLightGray),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                fontFamily = jansilFont,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}
