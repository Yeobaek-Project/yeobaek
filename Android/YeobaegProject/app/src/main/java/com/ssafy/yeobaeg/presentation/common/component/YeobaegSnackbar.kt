package com.ssafy.yeobaeg.presentation.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGreen
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkRed
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

enum class SnackbarType {
    SUCCESS,
    ERROR,
    INFO
}

/**
 * 여백 앱 테마에 맞는 커스텀 스낵바
 *
 * @param message 표시할 메시지
 * @param type 스낵바 타입 (SUCCESS, ERROR, INFO)
 * @param modifier Modifier
 */
@Composable
fun YeobaegSnackbar(
    message: String,
    type: SnackbarType = SnackbarType.INFO,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor, icon) = when (type) {
        SnackbarType.SUCCESS -> Triple(
            YeobaegDarkGreen,
            YeobaegWhite,
            Icons.Default.CheckCircle
        )
        SnackbarType.ERROR -> Triple(
            YeobaegDarkRed,
            YeobaegWhite,
            Icons.Default.Warning
        )
        SnackbarType.INFO -> Triple(
            YeobaegDarkBrown,
            YeobaegWhite,
            null
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = message,
                color = contentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * SnackbarHost와 함께 사용하기 위한 커스텀 Snackbar
 */
@Composable
fun YeobaegSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = { data ->
            // visuals에서 타입 정보를 가져오기 위해 message 파싱
            // 메시지 형식: "TYPE:actual_message"
            val (type, message) = parseSnackbarMessage(data.visuals.message)
            YeobaegSnackbar(
                message = message,
                type = type
            )
        }
    )
}

/**
 * 메시지에서 타입과 실제 메시지 파싱
 * 형식: "SUCCESS:로그인 성공!" -> (SUCCESS, "로그인 성공!")
 */
private fun parseSnackbarMessage(message: String): Pair<SnackbarType, String> {
    return when {
        message.startsWith("SUCCESS:") ->
            SnackbarType.SUCCESS to message.removePrefix("SUCCESS:")
        message.startsWith("ERROR:") ->
            SnackbarType.ERROR to message.removePrefix("ERROR:")
        message.startsWith("INFO:") ->
            SnackbarType.INFO to message.removePrefix("INFO:")
        else ->
            SnackbarType.INFO to message
    }
}

/**
 * Snackbar 표시를 위한 확장 함수
 */
suspend fun SnackbarHostState.showYeobaegSnackbar(
    message: String,
    type: SnackbarType = SnackbarType.INFO,
    duration: SnackbarDuration = SnackbarDuration.Short
): SnackbarResult {
    val prefixedMessage = when (type) {
        SnackbarType.SUCCESS -> "SUCCESS:$message"
        SnackbarType.ERROR -> "ERROR:$message"
        SnackbarType.INFO -> "INFO:$message"
    }
    return showSnackbar(
        message = prefixedMessage,
        duration = duration
    )
}

// ==================== Previews ====================

@Preview(showBackground = true, name = "성공 스낵바")
@Composable
private fun YeobaegSuccessSnackbarPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        YeobaegSnackbar(
            message = "로그인 성공!",
            type = SnackbarType.SUCCESS
        )
    }
}

@Preview(showBackground = true, name = "에러 스낵바")
@Composable
private fun YeobaegErrorSnackbarPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        YeobaegSnackbar(
            message = "네트워크 오류가 발생했습니다",
            type = SnackbarType.ERROR
        )
    }
}

@Preview(showBackground = true, name = "정보 스낵바")
@Composable
private fun YeobaegInfoSnackbarPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        YeobaegSnackbar(
            message = "새로운 업데이트가 있습니다",
            type = SnackbarType.INFO
        )
    }
}

@Preview(showBackground = true, name = "긴 메시지 스낵바")
@Composable
private fun YeobaegLongMessageSnackbarPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        YeobaegSnackbar(
            message = "회원가입에 성공했습니다! 이제 로그인하여 서비스를 이용하실 수 있습니다.",
            type = SnackbarType.SUCCESS
        )
    }
}

@Preview(showBackground = true, name = "스낵바 비교")
@Composable
private fun YeobaegSnackbarComparisonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        YeobaegSnackbar(
            message = "로그인 성공!",
            type = SnackbarType.SUCCESS
        )
        YeobaegSnackbar(
            message = "네트워크 오류",
            type = SnackbarType.ERROR
        )
        YeobaegSnackbar(
            message = "새 메시지 도착",
            type = SnackbarType.INFO
        )
    }
}
