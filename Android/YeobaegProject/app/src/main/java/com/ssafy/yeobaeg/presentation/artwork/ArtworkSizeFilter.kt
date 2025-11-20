// ArtworkFilterSheet.kt
package com.ssafy.yeobaeg.presentation.artwork

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTextField
import com.ssafy.yeobaeg.ui.theme.YeobaegGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkFilterSheet(
    visible: Boolean,
    maxWidthCm: Int?,
    maxHeightCm: Int?,
    onApply: (Int?, Int?) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var wText by remember(maxWidthCm) { mutableStateOf(maxWidthCm?.toString() ?: "") }
    var hText by remember(maxHeightCm) { mutableStateOf(maxHeightCm?.toString() ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text(text = "필터", fontWeight = FontWeight.SemiBold)
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = YeobaegGray)

            Text(text = "최대 크기", fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                YeobaegTextField(
                    value = wText,
                    placeholder = "가로(cm)",
                    onValueChange = { wText = it.filter(Char::isDigit).take(4) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Text("×", fontSize = 16.sp)
                YeobaegTextField(
                    value = hText,
                    placeholder = "세로(cm)",
                    onValueChange = { hText = it.filter(Char::isDigit).take(4) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                YeobaegOutlinedButton(
                    text = "초기화",
                    onClick = {
                        onApply(null, null)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                )
                YeobaegButton(
                    text = "적용",
                    onClick = {
                        onApply(wText.toIntOrNull(), hText.toIntOrNull())
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
