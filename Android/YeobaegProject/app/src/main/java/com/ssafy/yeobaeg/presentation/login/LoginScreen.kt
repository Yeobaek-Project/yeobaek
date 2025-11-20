package com.ssafy.yeobaeg.presentation.login

import androidx.compose.material3.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTextField
import com.ssafy.yeobaeg.presentation.common.component.YeobaegErrorDialog
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedTextField
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray

/**
 * Hilt ViewModel 버전
 * ViewModel을 DI 받아서 State를 UI 버전에 넘겨주는 역할
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginScreen(
        ui = uiState,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onLoginClick = viewModel::login,
        onLoginTestUser1 = { viewModel.loginWithTestUser("user4@example.com", "Password123!") },
        onLoginTestUser2 = { viewModel.loginWithTestUser("user2@example.com", "Password123!") },
        onLoginTestUser3 = { viewModel.loginWithTestUser("user3@example.com", "Password123!") },
        onNavigateSignUp = viewModel::navigateSignUp,
        onClearError = viewModel::clearError
    )
}

/**
 * 순수 UI 버전 (오버로딩)
 * 프리뷰나 테스트 시 ViewModel 없이 사용할 수 있음
 */
@Composable
fun LoginScreen(
    ui: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onLoginTestUser1: () -> Unit,
    onLoginTestUser2: () -> Unit,
    onLoginTestUser3: () -> Unit,
    onNavigateSignUp: () -> Unit,
    onClearError: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_title),
                contentDescription = "여백 로고",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Column {
                YeobaegOutlinedTextField(
                    value = ui.email,
                    onValueChange = onEmailChange,
                    placeholder = "이메일을 입력하세요",
                    modifier = Modifier.fillMaxWidth(),
                    title = "이메일",
                    essential = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column {
                YeobaegOutlinedTextField(
                    value = ui.password,
                    onValueChange = onPasswordChange,
                    placeholder = "비밀번호를 입력하세요",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    title = "비밀번호",
                    essential = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            YeobaegButton(
                text = "로그인",
                onClick = onLoginClick,
                enabled = !ui.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "━━━━━━ 테스트 로그인 ━━━━━━",
                color = YeobaegTextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                YeobaegButton(
                    text = "카페 AR",
                    onClick = onLoginTestUser1,
                    enabled = !ui.isLoading,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                YeobaegButton(
                    text = "작가",
                    onClick = onLoginTestUser2,
                    enabled = !ui.isLoading,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                YeobaegButton(
                    text = "사장",
                    onClick = onLoginTestUser3,
                    enabled = !ui.isLoading,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = YeobaegTextGray)) {
                    append("계정이 없으신가요? ")
                }
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "SIGNUP",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = YeobaegDarkBrown,
                                fontWeight = FontWeight.Bold
                            )
                        ),
                        linkInteractionListener = { onNavigateSignUp() }
                    )
                ) {
                    append("회원가입")
                }
            }

            Text(
                text = annotatedString,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        if (ui.isLoading) {
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
        ui.error?.let { errorMessage ->
            YeobaegErrorDialog(
                message = errorMessage,
                onConfirm = onClearError
            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        ui = LoginUiState(email = "test@example.com", password = "", isLoading = false),
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onLoginTestUser1 = {},
        onLoginTestUser2 = {},
        onLoginTestUser3 = {},
        onNavigateSignUp = {},
        onClearError = {}
    )
}
