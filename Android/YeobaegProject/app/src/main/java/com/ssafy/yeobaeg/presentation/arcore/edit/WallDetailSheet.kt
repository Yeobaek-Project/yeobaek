package com.ssafy.yeobaeg.presentation.arcore.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTextField

@Composable
fun WallDetailSheet(
    modifier: Modifier = Modifier,
    wall: DisplaySlot,
    allMeasured: Boolean,
    onNameChange: (String) -> Unit,
    onMeasureClick: () -> Unit,
    onNextClick: () -> Unit,
    onApplyHeightToAll: () -> Unit // 추가
) {
    var name by remember(wall.wid) { mutableStateOf(wall.wName) }

    Column(modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "벽 상세 정보",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (wall.height > 0) {
                TextButton(
                    onClick = onApplyHeightToAll,
                ) {
                    Text(
                        text = "측정된 높이를 모든 벽에 적용",
                        color = Color(0xFF64B5F6), // 연한 파란색
                        fontSize = 13.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        LabeledValueRow(label = "길이", value = wall.widthText)
        LabeledValueRow(
            label = "높이",
            value = if (wall.height > 0) wall.heightText else "미측정"
        )

        // 이름
        Spacer(Modifier.height(8.dp))
        YeobaegTextField(
            value = name,
            onValueChange = {
                name = it.take(10)
                onNameChange(it.take(10))
            },
            label = "벽 이름",
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(Modifier.height(16.dp))

        // AR 측정 버튼
        YeobaegButton(
            text = "AR로 높이 측정",
            onClick = onMeasureClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Spacer(Modifier.height(12.dp))

        // 다음 벽
        if (!allMeasured) {
            YeobaegOutlinedButton(
                text = "다음 벽",
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        } else {
            YeobaegButton(
                text = "완료",
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
}

/** Label + value 한 줄 */
@Composable
private fun LabeledValueRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Normal)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}