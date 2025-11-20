package com.ssafy.yeobaeg.presentation.chat.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegStepBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingFeeScreen(
    viewModel: MatchingViewModel = hiltViewModel()
) {
    var selectedFee by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val feeOptions = (0..15).toList()

    Scaffold(
        topBar = { YeobaegStepBar(4, 5) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YeobaegWhite)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                YeobaegButton(
                    text = "다음",
                    onClick = {
                        selectedFee?.let {
                            viewModel.setCommission(it.toDouble())
                        }
                    }, enabled = selectedFee != null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        MatchingFeeContent(
            modifier = Modifier.padding(innerPadding),
            expanded = expanded,
            onExpandedChange = { expanded = it },
            selectedFee = selectedFee,
            onSelectFee = { selectedFee = it },
            feeOptions = feeOptions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingFeeContent(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedFee: Int?,
    onSelectFee: (Int) -> Unit,
    feeOptions: List<Int>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YeobaegWhite)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = "수수료는 몇 %인가요?",
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = jansilFont
        )
        Spacer(Modifier.height(24.dp))

        Text(
            text = "수수료(%)",
            color = YeobaegTextGray,
            fontSize = 14.sp,
            fontFamily = jansilFont
        )

        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange(!expanded) }
        ) {
            OutlinedTextField(
                value = selectedFee?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        text = "0~15 중 선택",
                        color = YeobaegTextGray,
                        fontFamily = jansilFont
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(56.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    fontFamily = jansilFont
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YeobaegBrown,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = YeobaegBrown,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.background(Color.White)
            ) {
                feeOptions.forEach { fee ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "$fee%",
                                fontFamily = jansilFont,
                                color = Color.Black
                            )
                        },
                        onClick = {
                            onSelectFee(fee)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatchingFeePreview() {
    MatchingFeeContent(
        expanded = false,
        onExpandedChange = {},
        selectedFee = 10,
        onSelectFee = {},
        feeOptions = (0..15).toList()
    )
}
