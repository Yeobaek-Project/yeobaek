package com.ssafy.yeobaeg.presentation.chat.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import com.ssafy.yeobaeg.domain.model.chat.MessageType
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.jansilFont
import com.ssafy.yeobaeg.presentation.common.util.DateFormatter
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

@Composable
fun BasicSystemMessage(
    message: ChatMessage,
    isMy: Boolean,
    partnerName: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(350.dp)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = YeobaegWhite,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, YeobaegLightGray.copy(alpha = 0.5f))
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val textHeader = when (message.messageType) {
                    MessageType.AGREED -> "매칭 수락 🎈"
                    MessageType.REJECTED -> "매칭 거절 🫸"
                    MessageType.CANCELLED -> "매칭 취소 ✖️"
                    else -> ""
                }

                Text(
                    text = textHeader,
                    fontFamily = jansilFont,
                    fontSize = 18.sp,
                    color = YeobaegBlack,
                    fontWeight = FontWeight.Bold
                )

                val textBody = when (message.messageType) {
                    MessageType.AGREED -> "매칭이 수락되었습니다!"
                    MessageType.REJECTED -> if (isMy) {
                        "${partnerName}님의 제안을 거절했습니다."
                    } else {
                        "당신의 제안이 거절되었습니다."
                    }

                    MessageType.CANCELLED -> "전시가 취소되었습니다."
                    else -> ""
                }

                Text(
                    text = textBody,
                    fontFamily = jansilFont,
                    fontSize = 16.sp,
                    color = YeobaegBlack
                )

                Text(
                    text = DateFormatter.toTimeAgo(message.createdAt),
                    fontSize = 11.sp,
                    color = YeobaegTextGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasicSystemMessagePreview() {
    val dummyMessage = ChatMessage(
        messageId = 1,
        senderId = 10,
        messageType = MessageType.AGREED,
        content = null,
        imageUrl = null,
        exhibitionId = null,
        isRead = false,
        createdAt = "2시간 전"
    )

    BasicSystemMessage(
        message = dummyMessage,
        isMy = false,
        partnerName = "아티스트"
    )
}

