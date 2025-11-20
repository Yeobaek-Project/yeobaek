package com.ssafy.yeobaeg.presentation.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGreen
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkRed
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkYellow
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextPrimary
import com.ssafy.yeobaeg.ui.theme.YeobaegTextSecondary
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

enum class DialogType {
    ERROR,
    WARNING,
    INFO,
    SUCCESS
}

/**
 * 여백 앱 테마에 맞는 커스텀 다이얼로그
 *
 * @param title 다이얼로그 제목
 * @param message 다이얼로그 메시지
 * @param type 다이얼로그 타입 (ERROR, WARNING, INFO, SUCCESS)
 * @param confirmText 확인 버튼 텍스트 (기본값: "확인")
 * @param dismissText 취소 버튼 텍스트 (null이면 버튼 숨김)
 * @param onConfirm 확인 버튼 클릭 시 콜백
 * @param onDismiss 취소 버튼 클릭 또는 다이얼로그 외부 클릭 시 콜백
 * @param dismissOnBackPress 뒤로가기 버튼으로 닫기 가능 여부 (기본값: true)
 * @param dismissOnClickOutside 외부 클릭으로 닫기 가능 여부 (기본값: true)
 */
@Composable
fun YeobaegDialog(
    title: String,
    message: String,
    type: DialogType = DialogType.INFO,
    confirmText: String = "확인",
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onConfirm,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    icon: ImageVector? = null
) {
    val (iconColor, iconVector) = when (type) {
        DialogType.ERROR -> YeobaegDarkRed to (icon ?: Icons.Default.Warning)
        DialogType.WARNING -> YeobaegDarkYellow to (icon ?: Icons.Default.Warning)
        DialogType.INFO -> YeobaegDarkBrown to icon
        DialogType.SUCCESS -> YeobaegDarkGreen to icon
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = YeobaegWhite,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 아이콘 (있는 경우만 표시)
                iconVector?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 제목
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = YeobaegTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 메시지
                Text(
                    text = message,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = YeobaegTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (dismissText != null) {
                        Arrangement.spacedBy(12.dp)
                    } else {
                        Arrangement.Center
                    }
                ) {
                    // 취소 버튼 (있는 경우만 표시)
                    dismissText?.let {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YeobaegLightGray,
                                contentColor = YeobaegTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = it,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 확인 버튼
                    Button(
                        onClick = onConfirm,
                        modifier = if (dismissText != null) {
                            Modifier
                                .weight(1f)
                                .height(48.dp)
                        } else {
                            Modifier
                                .widthIn(min = 120.dp)
                                .height(48.dp)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YeobaegDarkBrown,
                            contentColor = YeobaegWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = confirmText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 에러 다이얼로그 (간편 함수)
 */
@Composable
fun YeobaegErrorDialog(
    message: String,
    title: String = "오류",
    onConfirm: () -> Unit
) {
    YeobaegDialog(
        title = title,
        message = message,
        type = DialogType.ERROR,
        onConfirm = onConfirm
    )
}

/**
 * 확인 다이얼로그 (확인/취소 버튼)
 */
@Composable
fun YeobaegConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "확인",
    dismissText: String = "취소",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    YeobaegDialog(
        title = title,
        message = message,
        type = DialogType.WARNING,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

// ==================== Previews ====================

@Preview(showBackground = true, name = "에러 다이얼로그")
@Composable
private fun YeobaegErrorDialogPreview() {
    YeobaegErrorDialog(
        message = "올바른 이메일 형식이 아닙니다",
        onConfirm = {}
    )
}

@Preview(showBackground = true, name = "경고 다이얼로그 - 확인/취소")
@Composable
private fun YeobaegConfirmDialogPreview() {
    YeobaegConfirmDialog(
        title = "로그아웃",
        message = "정말 로그아웃 하시겠습니까?",
        confirmText = "로그아웃",
        dismissText = "취소",
        onConfirm = {},
        onDismiss = {}
    )
}

@Preview(showBackground = true, name = "정보 다이얼로그")
@Composable
private fun YeobaegInfoDialogPreview() {
    YeobaegDialog(
        title = "안내",
        message = "이 기능은 작가 계정에서만 사용할 수 있습니다.",
        type = DialogType.INFO,
        onConfirm = {}
    )
}

@Preview(showBackground = true, name = "성공 다이얼로그")
@Composable
private fun YeobaegSuccessDialogPreview() {
    YeobaegDialog(
        title = "완료",
        message = "작품이 성공적으로 등록되었습니다!",
        type = DialogType.SUCCESS,
        confirmText = "확인",
        onConfirm = {}
    )
}

@Preview(showBackground = true, name = "긴 메시지 다이얼로그")
@Composable
private fun YeobaegLongMessageDialogPreview() {
    YeobaegDialog(
        title = "네트워크 오류",
        message = "서버와의 연결이 원활하지 않습니다.\n잠시 후 다시 시도해주세요.\n문제가 계속되면 고객센터로 문의해주세요.",
        type = DialogType.ERROR,
        onConfirm = {}
    )
}
