package com.ssafy.yeobaeg.presentation.common.component

import android.R.attr.enabled
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray

/**
 * 여백 앱 공통 버튼 컴포넌트
 */
@Composable
fun YeobaegButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bgColor: Color = YeobaegBrown,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            disabledContainerColor = YeobaegTextGray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


@Composable
fun YeobaegButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = YeobaegDarkBrown,
            disabledContainerColor = YeobaegTextGray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}


@Composable
fun YeobaegOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes icon: Int? = null,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = YeobaegDarkGray,
            disabledContentColor = YeobaegTextGray.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = if (enabled) YeobaegGray else YeobaegGray.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, name = "기본 버튼")
@Composable
private fun YeobaegButtonPreview() {
    YeobaegButton(
        text = "확인",
        onClick = {}
    )
}

@Preview(showBackground = true, name = "비활성화 버튼")
@Composable
private fun YeobaegButtonDisabledPreview() {
    YeobaegButton(
        text = "확인",
        onClick = {},
        enabled = false
    )
}

@Preview(showBackground = true, name = "아웃라인 버튼")
@Composable
private fun YeobaegOutlinedButtonPreview() {
    YeobaegOutlinedButton(
        text = "취소",
        onClick = {}
    )
}

@Preview(showBackground = true, name = "아웃라인 버튼 - 아이콘 있음")
@Composable
private fun YeobaegOutlinedButtonWithIconPreview() {
    YeobaegOutlinedButton(
        text = "카페 추가",
        icon = android.R.drawable.ic_input_add, // 실제 아이콘으로 변경
        onClick = {}
    )
}

@Preview(showBackground = true, name = "아웃라인 버튼 - 비활성화")
@Composable
private fun YeobaegOutlinedButtonDisabledPreview() {
    YeobaegOutlinedButton(
        text = "비활성화",
        onClick = {},
        enabled = false
    )
}

@Preview(showBackground = true, name = "버튼 비교")
@Composable
private fun YeobaegButtonsComparisonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        YeobaegButton(
            text = "기본 버튼",
            onClick = {}
        )

        YeobaegOutlinedButton(
            text = "아웃라인 버튼",
            onClick = {}
        )

        YeobaegOutlinedButton(
            text = "아이콘 버튼",
            icon = android.R.drawable.ic_menu_add,
            onClick = {}
        )
    }
}