package com.ssafy.yeobaeg.presentation.chat.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegStepBar
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingNoteScreen(
    viewModel: MatchingViewModel = hiltViewModel()
) {
    var noteText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()


    Scaffold(
        topBar = { YeobaegStepBar(5, 5) },
        bottomBar = {
            MatchingNoteBottomBar(
                onSubmit = {
                    viewModel.setRequirements(noteText)
                    viewModel.requestMatching()
                },
                enabled = !uiState.isLoading
            )
        }
    ) { innerPadding ->
        MatchingNoteContent(
            modifier = Modifier.padding(innerPadding),
            noteText = noteText,
            onNoteChange = { if (it.length <= 300) noteText = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingNoteContent(
    modifier: Modifier = Modifier,
    noteText: String,
    onNoteChange: (String) -> Unit
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
            text = "파트너에게 전하고 싶은 말이 있나요?",
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = jansilFont
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = noteText,
            onValueChange = onNoteChange,
            placeholder = {
                Text(
                    text = "수수료 부분은 협의 가능해요.",
                    color = YeobaegTextGray,
                    fontSize = 14.sp,
                    fontFamily = jansilFont
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = jansilFont
            ),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFDCC8A5),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                cursorColor = Color(0xFFDCC8A5),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            maxLines = 6
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${noteText.length}/300",
            color = YeobaegTextGray,
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
            fontFamily = jansilFont
        )
    }
}

@Composable
fun MatchingNoteBottomBar(
    onSubmit: () -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YeobaegWhite)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        YeobaegButton(
            text = "매칭 요청",
            onClick = onSubmit,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )

    }
}

@Preview(showBackground = true)
@Composable
fun MatchingNotePreview() {
    MatchingNoteContent(
        noteText = "저는 주로 작품을 조용히 감상하는 편이에요.\n관람 후에 카페에서 감상을 나누는 것도 좋아요!",
        onNoteChange = {}
    )
}
