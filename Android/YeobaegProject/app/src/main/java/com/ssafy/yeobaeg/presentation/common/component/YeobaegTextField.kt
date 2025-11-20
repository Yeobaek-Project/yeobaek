package com.ssafy.yeobaeg.presentation.common.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray

/**
 * 여백 앱 공통 TextField 컴포넌트 (라벨 포함)
 */
@Composable
fun YeobaegTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    showLabel: Boolean = true,
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLine:Int = 1,
    maxLine:Int = if (singleLine) 1 else Int.MAX_VALUE,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onFocusTextField: (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        // 라벨
        if (showLabel && label != null) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // TextField
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { 
                Text(
                    text = placeholder, 
                    color = YeobaegTextGray.copy(0.5f)
                ) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .onFocusChanged { state ->
                    if (state.isFocused) {
                        onFocusTextField?.invoke()
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = YeobaegLightGray,
                focusedBorderColor = YeobaegDarkBrown,
                unfocusedContainerColor = YeobaegLightGray,
                focusedContainerColor = Color.White
            ),
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            minLines = minLine,
            maxLines = maxLine,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            readOnly = readOnly
        )
    }
}

