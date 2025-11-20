package com.ssafy.yeobaeg.presentation.signup

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.domain.model.Gender
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedTextField
import com.ssafy.yeobaeg.presentation.common.component.YeobaegStepBar
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.component.YeobaegErrorDialog
import com.ssafy.yeobaeg.presentation.common.util.DateFormatter
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegProjectTheme
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import java.util.Calendar

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 상단 바 (뒤로가기 버튼 포함)
            YeobaegTopAppBar(
                title = "회원가입",
                onBackClick = {
                    if (uiState.currentStep == 1) {
                        viewModel.onBack()
                    } else {
                        viewModel.previousStep()
                    }
                },
                actionIcon = null,
                onActionClick = {}
            )
            
            // 프로그래스 바
            YeobaegStepBar(
                step = uiState.currentStep,
                totalStep = uiState.totalSteps
            )

            when (uiState.currentStep) {
                1 -> Step1EmailPassword(
                    email = uiState.email,
                    password = uiState.password,
                    passwordConfirm = uiState.passwordConfirm,
                    emailError = uiState.emailError,
                    passwordError = uiState.passwordError,
                    passwordConfirmError = uiState.passwordConfirmError,
                    isEmailChecking = uiState.isEmailChecking,
                    isEmailAvailable = uiState.isEmailAvailable,
                    emailCheckMessage = uiState.emailCheckMessage,
                    onEmailChange = viewModel::updateEmail,
                    onPasswordChange = viewModel::updatePassword,
                    onPasswordConfirmChange = viewModel::updatePasswordConfirm,
                    checkEmailAvailability = viewModel::checkEmailAvailability,
                    onNextClick = viewModel::nextStep,
                    canProceed = viewModel.canProceedFromEmailPasswordStep()
                )
                2 -> Step2RoleSelection(
                    selectedRole = uiState.userRole,
                    onRoleSelect = viewModel::selectUserRole,
                    onNextClick = viewModel::nextStep,
                    canProceed = viewModel.canProceedFromRoleSelectionStep()
                )
                3 -> Step3GenderAndAge(
                    birthDate = uiState.birthDate,
                    selectedGender = uiState.gender,
                    birthDateError = uiState.birthDateError,
                    onBirthDateChange = viewModel::updateBirthDate,
                    onGenderSelect = viewModel::selectGender,
                    onNextClick = viewModel::nextStep,
                    canProceed = viewModel.canProceedFromGenderAndAgeStep()
                )
                4 -> Step4Nickname(
                    nickname = uiState.nickname,
                    userRole = uiState.userRole,
                    nicknameError = uiState.nicknameError,
                    onNicknameChange = viewModel::updateNickname,
                    onNextClick = viewModel::nextStep,
                    canProceed = viewModel.canProceedFromNicknameStep()
                )
            }
        }

        // 로딩 인디케이터
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = YeobaegDarkBrown)
            }
        }

        // 에러 다이얼로그
        uiState.error?.let { errorMessage ->
            YeobaegErrorDialog(
                message = errorMessage,
                onConfirm = viewModel::clearError
            )
        }
    }
}

@Composable
private fun Step1EmailPassword(
    email: String,
    password: String,
    passwordConfirm: String,
    emailError: String?,
    passwordError: String?,
    passwordConfirmError: String?,
    isEmailChecking: Boolean,
    isEmailAvailable: Boolean?,
    emailCheckMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    checkEmailAvailability: () -> Unit,
    onNextClick: () -> Unit,
    canProceed: Boolean
) {
    // 이메일 입력 완료 후 자동으로 중복 확인
    androidx.compose.runtime.LaunchedEffect(email) {
        if (email.isNotBlank() && emailError == null) {
            kotlinx.coroutines.delay(800) // 0.8초 debounce
            checkEmailAvailability()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        YeobaegOutlinedTextField(
            title = "이메일",
            essential = true,
            value = email,
            onValueChange = onEmailChange,
            placeholder = "coffee@gmail.com",
            modifier = Modifier.fillMaxWidth()
        )

        if (emailError != null) {
            Text(
                text = emailError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        if (isEmailChecking) {
            Text(
                text = "이메일 확인 중...",
                color = YeobaegTextGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        } else if (emailCheckMessage != null) {
            Text(
                text = emailCheckMessage,
                color = if (isEmailAvailable == true) YeobaegDarkBrown else Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        YeobaegOutlinedTextField(
            title = "비밀번호 (8자 이상, 영문+숫자+특수문자)",
            essential = true,
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Password123!",
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        
        if (passwordError != null) {
            Text(
                text = passwordError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        YeobaegOutlinedTextField(
            title = "비밀번호 확인",
            essential = true,
            value = passwordConfirm,
            onValueChange = onPasswordConfirmChange,
            placeholder = "비밀번호를 다시 입력하세요",
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        
        if (passwordConfirmError != null) {
            Text(
                text = passwordConfirmError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        YeobaegButton(
            text = "다음",
            onClick = onNextClick,
            enabled = canProceed
        )
    }
}

@Composable
private fun Step2RoleSelection(
    selectedRole: UserRole?,
    onRoleSelect: (UserRole) -> Unit,
    onNextClick: () -> Unit,
    canProceed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "어떤 역할로 시작하시겠어요?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "역할에 따라 제공되는 서비스가 달라집니다.",
            fontSize = 14.sp,
            color = YeobaegTextGray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 일반 사용자
        RoleSelectionItem(
            role = UserRole.GENERAL,
            description = "카페를 탐색하고 작품을 작품을 감상할 수 있어요.",
            iconBackgroundColor = Color(0xFFB8E6D5),
            iconColor = Color(0xFF2E7D5E),
            isSelected = selectedRole == UserRole.GENERAL,
            onSelect = { onRoleSelect(UserRole.GENERAL) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 신진 작가
        RoleSelectionItem(
            role = UserRole.ARTIST,
            description = "작품을 전시하고 카페에 전시할 수 있어요",
            iconBackgroundColor = Color(0xFFE5D4F7),
            iconColor = Color(0xFF8B5FBF),
            isSelected = selectedRole == UserRole.ARTIST,
            onSelect = { onRoleSelect(UserRole.ARTIST) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 카페 사장
        RoleSelectionItem(
            role = UserRole.CAFE_OWNER,
            description = "전시 공간을 제공하고 관리할 수 있어요.",
            iconBackgroundColor = Color(0xFFD4E8F7),
            iconColor = Color(0xFF4A90C9),
            isSelected = selectedRole == UserRole.CAFE_OWNER,
            onSelect = { onRoleSelect(UserRole.CAFE_OWNER) }
        )

        Spacer(modifier = Modifier.weight(1f))

        YeobaegButton(
            text = "다음",
            onClick = onNextClick,
            enabled = canProceed
        )
    }
}

@Composable
private fun RoleSelectionItem(
    role: UserRole,
    description: String,
    iconBackgroundColor: Color,
    iconColor: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icons.Default.Person.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 텍스트
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = role.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = YeobaegTextGray,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 라디오 버튼
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (isSelected) YeobaegDarkBrown else Color.LightGray,
                    shape = CircleShape
                )
                .background(if (isSelected) YeobaegDarkBrown else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step3GenderAndAge(
    birthDate: String,
    selectedGender: Gender?,
    birthDateError: String?,
    onBirthDateChange: (String) -> Unit,
    onGenderSelect: (Gender) -> Unit,
    onNextClick: () -> Unit,
    canProceed: Boolean
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "성별과 나이를 입력해 주세요",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "생년월일",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 날짜 선택 필드
        OutlinedTextField(
            value = birthDate.ifEmpty { "날짜를 선택하세요" },
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            enabled = false,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "날짜 선택",
                        tint = YeobaegDarkBrown
                    )
                }
            },
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = YeobaegTextGray,
                disabledTrailingIconColor = YeobaegDarkBrown
            )
        )
        
        if (birthDateError != null) {
            Text(
                text = birthDateError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "성별",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GenderButton(
                gender = Gender.MALE,
                isSelected = selectedGender == Gender.MALE,
                onSelect = { onGenderSelect(Gender.MALE) },
                modifier = Modifier.weight(1f)
            )
            GenderButton(
                gender = Gender.FEMALE,
                isSelected = selectedGender == Gender.FEMALE,
                onSelect = { onGenderSelect(Gender.FEMALE) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        YeobaegButton(
            text = "다음",
            onClick = onNextClick,
            enabled = canProceed
        )
    }
    
    // DatePicker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (birthDate.isNotEmpty()) {
                DateFormatter.parseFromApiFormat(birthDate)?.time
            } else {
                Calendar.getInstance().apply {
                    set(Calendar.YEAR, 1990)
                    set(Calendar.MONTH, Calendar.JANUARY)
                    set(Calendar.DAY_OF_MONTH, 1)
                }.timeInMillis
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = millis
                            }
                            val formattedDate = DateFormatter.formatToApiFormat(calendar)
                            onBirthDateChange(formattedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("확인", color = YeobaegDarkBrown)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소", color = YeobaegTextGray)
                }
            },
            colors = androidx.compose.material3.DatePickerDefaults.colors(
                containerColor = Color.White
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = androidx.compose.material3.DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = YeobaegDarkBrown,
                    headlineContentColor = YeobaegDarkBrown,
                    weekdayContentColor = YeobaegTextGray,
                    subheadContentColor = YeobaegDarkBrown,
                    yearContentColor = YeobaegTextGray,
                    currentYearContentColor = YeobaegDarkBrown,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = YeobaegDarkBrown,
                    dayContentColor = Color.Black,
                    disabledDayContentColor = YeobaegLightGray,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = YeobaegDarkBrown,
                    todayContentColor = YeobaegDarkBrown,
                    todayDateBorderColor = YeobaegDarkBrown,
                    dayInSelectionRangeContentColor = YeobaegDarkBrown,
                    dayInSelectionRangeContainerColor = YeobaegDarkBrown.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
private fun GenderButton(
    gender: Gender,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) YeobaegDarkBrown.copy(alpha = 0.1f) else YeobaegLightGray)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) YeobaegDarkBrown else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isSelected) YeobaegDarkBrown else YeobaegTextGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = gender.displayName,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) YeobaegDarkBrown else Color.Black
            )
        }
    }
}

@Composable
private fun Step4Nickname(
    nickname: String,
    userRole: UserRole?,
    nicknameError: String?,
    onNicknameChange: (String) -> Unit,
    onNextClick: () -> Unit,
    canProceed: Boolean
) {
    val label = when (userRole) {
        UserRole.ARTIST -> "작가 닉네임"
        UserRole.CAFE_OWNER -> "닉네임"
        else -> "닉네임"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "${label}을 입력해 주세요",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(24.dp))
        YeobaegOutlinedTextField(
            title = null,
            essential = false,
            value = nickname,
            onValueChange = onNicknameChange,
            placeholder = "커피왕자",
            modifier = Modifier.fillMaxWidth()
        )
        
        if (nicknameError != null) {
            Text(
                text = nicknameError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        YeobaegButton(
            text = "회원가입",
            onClick = onNextClick,
            enabled = canProceed
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep1Preview() {
    YeobaegProjectTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            YeobaegTopAppBar(
                title = "회원가입",
                onBackClick = {},
                actionIcon = null,
                onActionClick = {}
            )
            YeobaegStepBar(step = 1, totalStep = 4)
            Step1EmailPassword(
                email = "",
                password = "",
                passwordConfirm = "",
                emailError = null,
                passwordError = null,
                passwordConfirmError = null,
                isEmailChecking = false,
                isEmailAvailable = null,
                emailCheckMessage = null,
                onEmailChange = {},
                onPasswordChange = {},
                onPasswordConfirmChange = {},
                checkEmailAvailability = {},
                onNextClick = {},
                canProceed = false
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep2Preview() {
    YeobaegProjectTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            YeobaegTopAppBar(
                title = "회원가입",
                onBackClick = {},
                actionIcon = null,
                onActionClick = {}
            )
            YeobaegStepBar(step = 2, totalStep = 4)
            Step2RoleSelection(
                selectedRole = UserRole.CAFE_OWNER,
                onRoleSelect = {},
                onNextClick = {},
                canProceed = true
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep3Preview() {
    YeobaegProjectTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            YeobaegTopAppBar(
                title = "회원가입",
                onBackClick = {},
                actionIcon = null,
                onActionClick = {}
            )
            YeobaegStepBar(step = 3, totalStep = 4)
            Step3GenderAndAge(
                birthDate = "1990-01-01",
                selectedGender = Gender.MALE,
                birthDateError = null,
                onBirthDateChange = {},
                onGenderSelect = {},
                onNextClick = {},
                canProceed = true
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep4ArtistPreview() {
    YeobaegProjectTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            YeobaegTopAppBar(
                title = "회원가입",
                onBackClick = {},
                actionIcon = null,
                onActionClick = {}
            )
            YeobaegStepBar(step = 4, totalStep = 4)
            Step4Nickname(
                nickname = "커피왕자",
                userRole = UserRole.ARTIST,
                nicknameError = null,
                onNicknameChange = {},
                onNextClick = {},
                canProceed = true
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpStep4CafeOwnerPreview() {
    YeobaegProjectTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            YeobaegTopAppBar(
                title = "회원가입",
                onBackClick = {},
                actionIcon = null,
                onActionClick = {}
            )
            YeobaegStepBar(step = 4, totalStep = 4)
            Step4Nickname(
                nickname = "커피왕자",
                userRole = UserRole.CAFE_OWNER,
                nicknameError = null,
                onNicknameChange = {},
                onNextClick = {},
                canProceed = true
            )
        }
    }
}

