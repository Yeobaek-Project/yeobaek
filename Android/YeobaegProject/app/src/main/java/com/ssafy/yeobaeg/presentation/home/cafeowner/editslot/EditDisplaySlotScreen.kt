package com.ssafy.yeobaeg.presentation.home.cafeowner.editslot

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.ui.SectionHeader
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditDisplaySlotScreen(
    cafeId: Long,
    slotId: Long,
    commissionRate: Int,
    requirements: String,
    viewModel: EditDisplaySlotViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(slotId) {
        viewModel.initSlotId(cafeId, slotId, commissionRate, requirements)
    }

    EditDisplaySlotScreen(
        state = state,
        onChangeDateRange = viewModel::updateDateRange,
        onChangeFee = viewModel::updateFeePercent,
        onChangeNote = viewModel::updateNote,
        onPrev = viewModel::onBackPressed,
        onSubmit = viewModel::patchSlotInfo
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDisplaySlotScreen(
    state: EditSlotState,
    onChangeDateRange: (Long?, Long?) -> Unit,
    onChangeFee: (Int) -> Unit,
    onChangeNote: (String) -> Unit,
    onPrev: () -> Unit,
    onSubmit: () -> Unit
) {
    val scroll = rememberScrollState()
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    trackColor = Color.White,
                    color = YeobaegLightBrown
                )
            }
        },
        bottomBar = {
            BottomBar(
                enabled = state.isValid,
                onPrev = onPrev,
                onSubmit = onSubmit
            )
        },
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0)
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            SectionHeader("전시 조건", "전시 운영 조건을 설정해주세요")
            Spacer(Modifier.height(8.dp))
//            FieldTitle("기본 전시 기간 *")
//            PeriodRangeField(
//                startAtMillis = state.startAtMillis,
//                endAtMillis = state.endAtMillis,
//                onSelected = onChangeDateRange
//            )
            Spacer(Modifier.height(8.dp))
            FieldTitle("수수료율 (%) *")
            FeeDropdown(
                current = state.feePercent,
                onSelect = onChangeFee
            )
            Text(
                "작품 판매 시 카페가 받는 수수료 비율입니다",
                fontSize = 12.sp,
                color = YeobaegDarkGray
            )
            Spacer(Modifier.height(8.dp))
            FieldTitle("추가 요청사항 (500자 이내)")
            OutlinedTextField(
                value = state.note,
                onValueChange = onChangeNote,
                placeholder = { Text("작가에게 전달하고 싶은 요청사항이나 카페 운영 방침을 입력해주세요...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                minLines = 5,
                maxLines = 10,
                supportingText = {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            "${state.note.length}/500",
                            fontSize = 12.sp,
                            color = YeobaegDarkGray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = YeobaegLightGray,
                    focusedBorderColor = Color.Black,
                    focusedContainerColor = Color.White
                )
            )
            Spacer(Modifier.height(8.dp))
            InfoBox()
        }
    }
}

@Composable
private fun BottomBar(
    enabled: Boolean,
    onPrev: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        YeobaegOutlinedButton(
            text = "이전",
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            onClick = onPrev
        )
        YeobaegButton(
            text = "등록 완료",
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            onClick = onSubmit,
            enabled = enabled
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodRangeField(
    startAtMillis: Long?,
    endAtMillis: Long?,
    onSelected: (Long?, Long?) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val brown = YeobaegLightBrown
    val brownRange = YeobaegLightBrown.copy(alpha = 0.18f)

    val format = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return@remember { m: Long? ->
                m?.let {
                    java.time.Instant.ofEpochMilli(it)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                        .format(fmt)
                } ?: "-"
            }
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return@remember { m: Long? ->
                m?.let { sdf.format(Date(it)) } ?: "-"
            }
        }
    }



    val display = if (startAtMillis != null && endAtMillis != null)
        "${format(startAtMillis)} ~ ${format(endAtMillis)}"
    else "선택해주세요"

    Box {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(open) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = YeobaegLightGray,
                focusedBorderColor = Color.Black,
                focusedContainerColor = Color.White
            )
        )
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .clickable { open = true }
        )
    }

    if (open) {
        val pickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startAtMillis,
            initialSelectedEndDateMillis = endAtMillis
        )

        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(
                    enabled = pickerState.selectedStartDateMillis != null &&
                            pickerState.selectedEndDateMillis != null,
                    onClick = {
                        val s = pickerState.selectedStartDateMillis
                        val e = pickerState.selectedEndDateMillis
                        onSelected(s, e)
                        open = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = brown)
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(
                    onClick = { open = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = brown)
                ) { Text("취소") }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = brown
            )
        ) {
            DateRangePicker(
                state = pickerState,
                showModeToggle = false,
                title = {
                    Text(
                        text = "전시 기간 선택",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                },
                headline = {
                    val s = pickerState.selectedStartDateMillis
                    val e = pickerState.selectedEndDateMillis
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("시작일", fontSize = 12.sp)
                                Text(format(s), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text("~", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Column(horizontalAlignment = Alignment.End) {
                                Text("종료일", fontSize = 12.sp)
                                Text(format(e), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    selectedDayContainerColor = brown,
                    dayInSelectionRangeContainerColor = brownRange,
                    dayInSelectionRangeContentColor = brown,
                    todayDateBorderColor = brown,
                    todayContentColor = Color.Black
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeeDropdown(
    current: Int?,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val display = current?.let { "$it%" } ?: "선택해주세요"
    val options = remember { (0..50).toList() }
    var tfWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val tfWidthDp = with(density) { tfWidthPx.toDp() }

    Box {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coords -> tfWidthPx = coords.size.width },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = YeobaegLightGray,
                focusedBorderColor = Color.Black,
                focusedContainerColor = Color.White
            )
        )
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(tfWidthDp).heightIn(max = 200.dp),
            containerColor = Color.White
        ) {
            val scroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .verticalScroll(scroll)
            ) {
                options.forEach { fee ->
                    DropdownMenuItem(
                        text = { Text(text = "$fee%", fontSize = 16.sp) },
                        onClick = {
                            onSelect(fee)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldTitle(text: String) {
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun InfoBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(YeobaegLightYellow.copy(alpha = 0.55f))
            .padding(16.dp)
    ) {
        Text(
            "등록 완료 후 절차",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = YeobaegBrown
        )
        Spacer(Modifier.height(8.dp))
        Bullet("관리자 검토 (1~2일 소요)")
        Bullet("승인 시 작가 매칭 시스템에 등록")
        Bullet("작가들이 전시 신청 가능")
    }
}

@Composable
private fun Bullet(text: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
        Text("•", fontSize = 14.sp, modifier = Modifier.width(14.dp))
        Text(text, fontSize = 14.sp)
    }
}


@Preview(showBackground = true, name = "전시 슬롯 등록 - 빈 상태")
@Composable
private fun PreviewEditDisplaySlotScreen_Empty() {
    MaterialTheme {
        EditDisplaySlotScreen(
            state = EditSlotState(
                startAtMillis = null,
                endAtMillis = null,
                feePercent = null,
                note = ""
            ),
            onChangeDateRange = { _, _ -> },
            onChangeFee = {},
            onChangeNote = {},
            onPrev = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true, name = "전시 슬롯 등록 - 입력 완료")
@Composable
private fun PreviewEditDisplaySlotScreen_Filled() {
    MaterialTheme {
        EditDisplaySlotScreen(
            state = EditSlotState(
                startAtMillis = 1704067200000L, // 2024-01-01
                endAtMillis = 1735689600000L,   // 2024-12-31
                feePercent = 20,
                note = "작품 전시 시 조명은 자연광을 활용하며, 매일 오전 청소 시간에 작품 관리를 부탁드립니다. 카페 운영 시간은 오전 9시부터 오후 10시까지입니다."
            ),
            onChangeDateRange = { _, _ -> },
            onChangeFee = {},
            onChangeNote = {},
            onPrev = {},
            onSubmit = {}
        )
    }
}
