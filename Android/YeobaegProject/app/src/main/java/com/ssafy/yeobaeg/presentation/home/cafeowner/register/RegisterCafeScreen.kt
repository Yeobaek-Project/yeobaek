// RegisterCafeScreen.kt
package com.ssafy.yeobaeg.presentation.home.cafeowner.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.cafe.CafeCategory
import com.ssafy.yeobaeg.domain.model.storage.PresignedUrl
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedTextField
import com.ssafy.yeobaeg.presentation.common.component.YeobaegStepBar
import com.ssafy.yeobaeg.presentation.common.ui.SectionHeader
import com.ssafy.yeobaeg.presentation.common.util.advancedImePadding
import com.ssafy.yeobaeg.presentation.common.util.rememberMultiImagePicker
import com.ssafy.yeobaeg.presentation.common.util.uriToTempFile
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import java.io.File

@Composable
fun RegisterCafeScreen(
    viewModel: RegisterCafeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    LaunchedEffect(Unit) { viewModel.getCafeCategories() }

    Scaffold(
        topBar = {
            if (state.step != STEP_ADDRESS) YeobaegStepBar(step = state.step, totalStep = TOTAL_STEPS)
        },
        bottomBar = {
            if (state.step != STEP_ADDRESS) BottomBarScaffold(
                step = state.step,
                enabled = if (state.step == STEP1) state.isStep1Valid else state.isStep2Valid,
                onPrev = { viewModel.prev() },
                onNext = { viewModel.next() }
            )
        },
        containerColor = Color.White
    ) { inner ->
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    .padding(inner)
                    .advancedImePadding()
                    .then(if (state.step > 0) Modifier.verticalScroll(scroll) else Modifier),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state.step) {
                    STEP1 -> Step1BasicInfo(
                        state = state,
                        onName = viewModel::updateName,
                        onDesc = viewModel::updateDescription,
                        onPhone = viewModel::updatePhone,
                        onOpen = viewModel::updateOpenTime,
                        onClose = viewModel::updateCloseTime,
                        onAddImage = viewModel::addImage,
                        onRemoveImage = viewModel::removeImage,
                        showAddressPicker = viewModel::openAddressPicker
                    )

                    STEP2 -> Step2Attributes(
                        state = state,
                        onToggleStyle = viewModel::toggleStyle,
                        onVisitors = viewModel::selectVisitorsBand
                    )

                    STEP_ADDRESS -> {
                        BackHandler { viewModel.closeAddressPicker() }
                        PostcodeJibunWebView(
                            modifier = Modifier.fillMaxSize(),
                            onClose = { viewModel.closeAddressPicker() },
                            onSelected = { selected ->
                                viewModel.updateAddress(selected)
                                viewModel.closeAddressPicker()
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}


@Composable
private fun BottomBarScaffold(
    step: Int,
    enabled: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (step == STEP2) {
            YeobaegOutlinedButton(
                text = "이전",
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                onClick = onPrev
            )
        }
        YeobaegButton(
            text = if (step == STEP1) "다음" else "완료",
            modifier = Modifier.weight(1f),
            onClick = onNext,
            enabled = enabled
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step1BasicInfo(
    state: RegisterCafeState,
    onName: (String) -> Unit,
    onDesc: (String) -> Unit,
    onPhone: (String) -> Unit,
    onOpen: (String) -> Unit,
    onClose: (String) -> Unit,
    onAddImage: (List<File>) -> Unit,
    onRemoveImage: (PresignedUrl) -> Unit,
    showAddressPicker: () -> Unit
) {
    SectionHeader(
        title = "기본 정보 입력",
        subtitle = "매장의 기본 정보를 입력해 주세요."
    )
    Spacer(Modifier.height(20.dp))
    Column {
        YeobaegOutlinedTextField(
            title = "매장명",
            essential = true,
            value = state.name,
            onValueChange = onName,
            placeholder = "Gallery Café Muse",
            modifier = Modifier.fillMaxWidth(),
        )
        val nameError = state.nameError
        if (nameError != null) {
            Text(
                text = nameError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
        Spacer(Modifier.height(20.dp))

        // 주소는 클릭 이벤트가 필요하므로 별도 처리
        YeobaegOutlinedTextField(
            title = "주소",
            value = state.address,
            onValueChange = { },
            essential = true,
            placeholder = "서울시 강남구 신사동 123-45",
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state ->
                    if (state.isFocused) {
                        showAddressPicker.invoke()
                    }
                },
        )

        Spacer(Modifier.height(20.dp))

        // 매장 소개 (multi-line)
        YeobaegOutlinedTextField(
            title = "매장 소개",
            value = state.description,
            essential = false,
            onValueChange = onDesc,
            placeholder = "예술과 커피가 만나는 특별한 공간입니다...",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            singleLine = false,
            minLine = 4,
            maxLine = 8
        )
        Spacer(Modifier.height(20.dp))

        YeobaegOutlinedTextField(
            title = "연락처",
            essential = true,
            value = state.phone,
            onValueChange = onPhone,
            placeholder = "02-1234-5678",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                YeobaegOutlinedTextField(
                    title = "오픈 시간",
                    essential = true,
                    value = state.openTime,
                    onValueChange = onOpen,
                    placeholder = "08:00",
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                YeobaegOutlinedTextField(
                    title = "마감 시간",
                    essential = true,
                    value = state.closeTime,
                    onValueChange = onClose,
                    placeholder = "22:00",
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        ImagePickerBox(
            images = state.uploadedImages,
            onAdd = onAddImage,
            onRemove = onRemoveImage
        )
    }
}

@Composable
private fun ImagePickerBox(
    images: List<PresignedUrl>,
    onAdd: (List<File>) -> Unit,
    onRemove: (PresignedUrl) -> Unit
) {
    val context = LocalContext.current

    val pickImages = rememberMultiImagePicker(max = 10) { uris ->
        val tempFiles = mutableListOf<File>()
        uris.forEach {
            tempFiles.add(uriToTempFile(context, it))
        }
        onAdd(tempFiles)
    }

    val border = BorderStroke(1.dp, Color(0xFFE0E3E8))
    Text(text = "매장 이미지 추가")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(border, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(images) { image ->
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F3F5))
                        .clickable { onRemove(image) },
                    contentAlignment = Alignment.Center
                ) { AsyncImage(model = image.downloadUrl, contentDescription = null) }
            }
            item {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF9FAFB))
                        .border(BorderStroke(1.dp, Color(0xFFE0E3E8)), RoundedCornerShape(12.dp))
                        .clickable { pickImages() },
                    contentAlignment = Alignment.Center
                ) { Text("이미지\n추가", color = Color(0xFF9EA4AA)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step2Attributes(
    state: RegisterCafeState,
    onToggleStyle: (CafeCategory) -> Unit,
    onVisitors: (VisitorsBand) -> Unit
) {
    SectionHeader(
        title = "매장 특성",
        subtitle = "매장의 특성과 분위기를 알려주세요."
    )
    Spacer(Modifier.height(20.dp))
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "인터리어 스타일 (다중 선택 가능, 최대 3개) *", fontSize = 14.sp)

        val styles = state.cafeCategories

        styles.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { style ->
                    val selected = style in state.selectedStyles
                    val limitReached = state.selectedStyles.size >= 3 && !selected
                    FilterChip(
                        selected = selected,
                        onClick = { if (!limitReached) onToggleStyle(style) },
                        enabled = !limitReached,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp),
                        colors = FilterChipDefaults.filterChipColors().copy(
                            selectedContainerColor = YeobaegDarkBrown.copy(alpha = 0.1f)
                        ),
                        label = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = style.categoryName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        Text(
            text = "선택된 스타일: ${state.selectedStyles.size} / 3",
            fontSize = 12.sp,
            color = Color(0xFF7A7F87),
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(Modifier.height(8.dp))
        Text(text = "일일 평균 방문객 수 *", fontSize = 14.sp)

        var expanded by remember { mutableStateOf(false) }
        val visitorText = state.visitorsBand?.label ?: "선택해주세요"
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = visitorText,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable)
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = YeobaegLightGray,
                    focusedBorderColor = Color.Black,
                    focusedContainerColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color.White
            ) {
                VisitorsBand.entries.forEach { band ->
                    DropdownMenuItem(
                        text = { Text(band.label) },
                        onClick = {
                            onVisitors(band)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(textColor = Color(0xFF111827))
                    )
                }
            }
        }
    }
}

// --------------------------- 프리뷰 ---------------------------
@Preview(showBackground = true, name = "Step 1 - 기본 정보 입력")
@Composable
private fun PreviewRegisterCafeScreen_Step1() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Step1BasicInfo(
                state = RegisterCafeState(
                    step = 1,
                ),
                onName = {},
                onDesc = {},
                onPhone = {},
                onOpen = {},
                onClose = {},
                onAddImage = {},
                onRemoveImage = {},
                showAddressPicker = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Step 1 - 정보 입력 중")
@Composable
private fun PreviewRegisterCafeScreen_Step1_Filled() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Step1BasicInfo(
                state = RegisterCafeState(
                    step = 1,
                    name = "아트카페 여백",
                    address = "서울시 강남구 신사동 123-45",
                    description = "예술과 커피가 만나는 특별한 공간입니다. 현대 미술 작품과 함께 여유로운 시간을 보내세요.",
                    phone = "02-1234-5678",
                    openTime = "09:00",
                    closeTime = "22:00",
                ),
                onName = {},
                onDesc = {},
                onPhone = {},
                onOpen = {},
                onClose = {},
                onAddImage = {},
                onRemoveImage = {},
                showAddressPicker = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Step 2 - 매장 특성")
@Composable
private fun PreviewStep2Attributes_Filled() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Step2Attributes(
                state = RegisterCafeState(
                    step = 2,
                    visitorsBand = VisitorsBand.OVER_500
                ),
                onToggleStyle = {},
                onVisitors = {}
            )
        }
    }
}
