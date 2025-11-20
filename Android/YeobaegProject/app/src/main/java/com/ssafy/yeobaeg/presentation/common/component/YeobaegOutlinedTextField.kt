package com.ssafy.yeobaeg.presentation.common.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun YeobaegOutlinedTextField(
    title:String?,
    essential:Boolean,
    value: String,
    unit:String?=null,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLine: Int = 1,
    maxLine: Int = if (singleLine) 1 else Integer.MAX_VALUE,
    fontFamily: FontFamily = jansilFont,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    if (title != null) {
        Row {
            Text(
                text = title,
                fontFamily=jansilFont,
                fontWeight = FontWeight.Normal
            )
            if(essential){
                Icon(
                    painter = painterResource(id = R.drawable.ic_essential),
                    contentDescription = "essential",
                    tint = Color(0xFFFF5A5A),
                    modifier = Modifier.size(10.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))

    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = {
            Text(
                text = placeholder,
                color = YeobaegTextGray,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFE0E0E0),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        trailingIcon = {  // 단위가 있을 때만 표시
            if (!unit.isNullOrBlank()) {
                Text(
                    text = unit,
                    color = YeobaegTextGray,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    )
}


@Preview(showBackground = true, showSystemUi = false)
@Composable
fun YeobaegOutlinedTextFieldPreview() {
    YeobaegOutlinedTextField(
        title = "작품 제목",
        essential = true,
        value = "",
        onValueChange = {},
        placeholder = "작품 제목을 입력하세요.",
        unit = "Kg"
    )
}