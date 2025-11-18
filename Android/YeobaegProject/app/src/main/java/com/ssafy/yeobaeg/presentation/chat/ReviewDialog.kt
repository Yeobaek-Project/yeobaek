package com.ssafy.yeobaeg.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedTextField
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown

@Composable
fun ReviewDialog(
    exhibitionId: Long,
    targetUserId: Long,
    viewModel: ChatViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = null,
        text = {
            Column {

                // 상단 타이틀 + 닫기 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "작가 리뷰하기",
                        fontFamily = jansilFont,
                        fontSize = 20.sp,
                        color = YeobaegBlack
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "닫기",
                        tint = YeobaegTextGray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }

                Text(
                    text = "작가와의 협업 경험을 평가해주세요",
                    fontFamily = jansilFont,
                    fontSize = 14.sp,
                    color = YeobaegTextGray
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "평가를 선택해주세요 *",
                    fontFamily = jansilFont,
                    fontSize = 15.sp,
                    color = YeobaegBlack
                )

                Spacer(Modifier.height(12.dp))

                // 별점 UI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        Icon(
                            painter = painterResource(
                                id = if (uiState.review.rating >= i)
                                    R.drawable.ic_star_filled
                                else
                                    R.drawable.ic_star
                            ),
                            contentDescription = null,
                            tint = YeobaegBrown,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    viewModel.updateRating(i)
                                }
                        )
                    }
                }

                Text(
                    text = uiState.review.rating.toString(),
                    fontFamily = jansilFont,
                    fontSize = 22.sp,
                    color = YeobaegBrown,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(12.dp))

                // 상세 의견
                YeobaegOutlinedTextField(
                    title = "상세 의견(선택사항)",
                    value = uiState.review.comment,
                    onValueChange = { viewModel.updateComment(it) },
                    placeholder = "작품 품질, 전문성, 소통 등에 대한 의견을 남겨주세요",
                    maxLine = 5,
                    essential = false,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    singleLine = false
                )

                Spacer(Modifier.height(20.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // 취소
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF2F2F2))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("취소", fontFamily = jansilFont, fontSize = 15.sp)
                    }

                    // 제출
                    val enabled = uiState.review.rating > 0

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (enabled) YeobaegBrown else Color(0xFFE0E0E0))
                            .clickable(enabled) {

                                // ★ ViewModel에서 API 호출
                                viewModel.submitReview(
                                    exhibitionId = exhibitionId,
                                    targetUserId = targetUserId
                                )

                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "리뷰 제출",
                            fontFamily = jansilFont,
                            fontSize = 15.sp,
                            color = if (enabled) Color.White else YeobaegTextGray
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}
