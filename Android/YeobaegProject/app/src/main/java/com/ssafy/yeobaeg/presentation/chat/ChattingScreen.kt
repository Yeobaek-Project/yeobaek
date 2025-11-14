package com.ssafy.yeobaeg.presentation.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import com.ssafy.yeobaeg.domain.model.chat.MessageType
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.util.DateFormatter
import com.ssafy.yeobaeg.presentation.common.util.clearFocusOnTap
import com.ssafy.yeobaeg.ui.theme.YeobaegBackground
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont
import kotlinx.coroutines.delay

private const val TAG = "ChattingScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChattingScreen(
    chatRoomId: Long,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val myUserId = viewModel.myUserId.collectAsState().value
    val myUserRole = viewModel.myUserRole.collectAsState().value
    var showMatchingFlow by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    DisposableEffect(chatRoomId) {
        viewModel.loadChatMessages(chatRoomId)
        viewModel.subscribeToChatRoom(chatRoomId)

        onDispose {
            viewModel.unsubscribeFromChatRoom(chatRoomId)
        }
    }


    ChattingScreenContent(
        title = uiState.chatRoomMessages.chatRoom.partnerName,
        profileImg = uiState.chatRoomMessages.chatRoom.partnerImageUrl,
        messages = uiState.chatRoomMessages.messages,
        partnerName = uiState.chatRoomMessages.chatRoom.partnerName,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        inputText = inputText,
        onInputChange = { inputText = it },
        onSendClick = { message ->
            if (message.isNotBlank()) {
                viewModel.sendMessage(message, myUserId)
                inputText = ""
            }
        },
        onBackClick = viewModel::navigateBack,
        onMatchingClick = {
            viewModel.navigateToMatching(
                uiState.chatRoomMessages.chatRoom.partnerId,
            )
        },
        myUserId = myUserId
    )
}

/**
 * 실제 UI를 구성하는 함수
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChattingScreenContent(
    title: String,
    profileImg: String?,
    partnerName: String,
    messages: List<ChatMessage> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    inputText: String = "",
    onInputChange: (String) -> Unit = {},
    onSendClick: (String) -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    onMatchingClick: () -> Unit = {},
    myUserId: Long?
) {
    val listState = rememberLazyListState()

    // 키보드가 올라올 때와 새 메시지가 추가될 때 자동 스크롤
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            // reverseLayout이므로 0번 인덱스가 최신 메시지
            delay(100) // 키보드 애니메이션 대기
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
//            .imePadding(), // 전체 Scaffold에 IME padding 적용
        topBar = {
            YeobaegTopAppBar(
                title = title,
                onBackClick = onBackClick ?: {},
                showBackButton = true
            )
        },
        bottomBar = {
            ChatInputBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YeobaegWhite)
                    .imePadding()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                value = inputText,
                onValueChange = onInputChange,
                onSendClick = onSendClick,
                onMatchingClick = onMatchingClick
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),

        ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegBackground)
                .padding(innerPadding)
        ) {
            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                errorMessage != null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage, color = Color.Red)
                }

                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .clearFocusOnTap()
                        .background(YeobaegWhite)
                        .padding(horizontal = 12.dp),
                    reverseLayout = true, // 최신 메시지가 아래에 표시되도록
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // reverseLayout이므로 역순으로 표시
                    items(
                        items = messages.asReversed(),
                        key = { "${it.messageId}_${it.createdAt}" }
                    ) { msg ->
                        when (msg.messageType) {

                            // 일반 텍스트 메시지
                            MessageType.TEXT -> {
                                val isMy = myUserId != null && msg.senderId == myUserId
                                if (isMy) MyMessageBubble(msg)
                                else PartnerMessageBubble(msg, profileImg)
                            }

                            // 이미지 메시지
                            MessageType.IMAGE -> {
//                                ImageMessageBubble(msg) // 너가 새로 만들 함수
                            }

                            // 전시 제안 메시지
                            MessageType.PROPOSAL -> {
                                ProposalMessageBubble(
                                    msg,
                                    partnerName = partnerName,
                                    myId = myUserId
                                )
                            }

                            // 전시 수락 / 거절 / 취소 / 시작 / 종료 (시스템 메시지)
                            MessageType.AGREED, MessageType.REJECTED, MessageType.CANCELLED, MessageType.STARTED, MessageType.ENDED -> {
                                SystemMessageBubble(msg)
                            }

                            // 서버가 추가 타입 보내도 UI 깨지지 않도록
                            else -> {
                                Text("지원하지 않는 메시지 타입: ${msg.messageType}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PartnerMessageBubble(message: ChatMessage, profileImg: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = profileImg,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .border(0.5.dp, YeobaegLightGray, CircleShape)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 12.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 12.dp
                ),
                color = YeobaegWhite,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .widthIn(min = 40.dp, max = 260.dp)
                    .wrapContentWidth()
            ) {
                Text(
                    text = message.content ?: "",
                    fontFamily = jansilFont,
                    color = YeobaegBlack,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
            Text(
                text = DateFormatter.toTimeAgo(message.createdAt),
                color = YeobaegTextGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ProposalMessageBubble(
    message: ChatMessage,
    myId: Long?,
    partnerName: String = "상대방",
    onAccept: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = YeobaegWhite,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, YeobaegLightGray.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // 상단 제목
            Text(
                text = "매칭 요청",
                fontFamily = jansilFont,
                fontSize = 16.sp,
                color = YeobaegBlack
            )

            Spacer(modifier = Modifier.height(13.dp))

            //내가 요청 보낸 경우
            if (message.senderId == myId) {
                Text(
                    text = "${partnerName}님에게 전시 매칭을 요청했습니다.",
                    fontFamily = jansilFont,
                    fontSize = 14.sp,
                    color = YeobaegBlack
                )
            } else { // 내가 요청 보내지 않은 경우
                // 본문
                Text(
                    text = "${partnerName}님이 전시 매칭을 요청했습니다.\n수락하시겠습니까?",
                    fontFamily = jansilFont,
                    fontSize = 14.sp,
                    color = YeobaegBlack
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 버튼 2개
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    // 거절 버튼
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .border(
                                width = 1.dp,
                                color = YeobaegLightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onReject?.invoke() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "거절",
                            fontFamily = jansilFont,
                            fontSize = 14.sp,
                            color = YeobaegBlack
                        )
                    }

                    // 수락 버튼
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(YeobaegBrown)
                            .clickable { onAccept?.invoke() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "수락",
                            fontFamily = jansilFont,
                            fontSize = 14.sp,
                            color = YeobaegWhite
                        )
                    }
                }
            }

        }
    }
}


@Composable
fun MyMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 4.dp
                ),
                color = YeobaegBrown,
                modifier = Modifier
                    .widthIn(min = 40.dp, max = 260.dp)
                    .wrapContentWidth()
            ) {
                Text(
                    text = message.content ?: "",
                    fontFamily = jansilFont,
                    color = YeobaegWhite,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
            Text(
                text = DateFormatter.toTimeAgo(message.createdAt),
                color = YeobaegTextGray,
                fontSize = 11.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onMatchingClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val trimmedValue = value.trim()
    val isSendEnabled = trimmedValue.isNotEmpty()
    var showDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // + 버튼
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = YeobaegLightGray.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable { showDialog = true })
                {
                    Text(
                        text = "+",
                        color = YeobaegTextGray,
                        fontSize = 24.sp
                    )
                }
            }

            // 입력 필드
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, YeobaegLightGray.copy(alpha = 0.5f)),
                color = YeobaegWhite
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = "메시지를 입력하세요",
                            color = YeobaegTextGray,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 42.dp, max = 120.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    ),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (isSendEnabled) {
                                onSendClick(trimmedValue)
                            }
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = YeobaegBrown
                    )
                )
            }

            // 전송 버튼
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = if (isSendEnabled) YeobaegBrown else YeobaegLightGray.copy(alpha = 0.3f)
            ) {
                IconButton(
                    onClick = {
                        if (isSendEnabled) {
                            onSendClick(trimmedValue)
                        }
                    },
                    enabled = isSendEnabled,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "보내기",
                        tint = if (isSendEnabled) Color.White else YeobaegTextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            title = null,
            text = {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DialogOption("사진", Color(0xFFE3F2FD)) { /* TODO: 사진 업로드 로직 */ showDialog =
                        false
                    }
                    DialogOption("매칭 요청", Color(0xFFFFF8E1)) {
                        showDialog = false
                        onMatchingClick()
                    }

                    DialogOption("매칭 요청", Color(0xFFFFF8E1)) { /* TODO: 매칭 로직 */ showDialog =
                        false
                    }
                }
            },
            containerColor = YeobaegWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SystemMessageBubble(message: ChatMessage) {
    Text(
        text = when (message.messageType) {
            MessageType.AGREED -> "상대가 제안을 수락했습니다."
            MessageType.REJECTED -> "상대가 제안을 거절했습니다."
            MessageType.CANCELLED -> "전시가 취소되었습니다."
            MessageType.STARTED -> "전시가 시작되었습니다."
            MessageType.ENDED -> "전시가 종료되었습니다."
            else -> ""
        },
        color = YeobaegTextGray,
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun DialogOption(text: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .background(color.copy(alpha = 0.4f))
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, fontSize = 16.sp, color = YeobaegBlack, fontFamily = jansilFont)
    }
}

@Preview(showBackground = true)
@Composable
fun ChattingScreenPreview() {
    val dummyMessages = listOf(
        ChatMessage(1, 1, MessageType.TEXT, "안녕하세요! 작품이 정말 마음에 들어요.", "", 0, null, false, "2시간 전"),
        ChatMessage(2, 0, MessageType.TEXT, "감사합니다 😊", "", 0, null, false, "2시간 전"),
        ChatMessage(3, 1, MessageType.TEXT, "전시 일정이 어떻게 되나요?", "", 0, null, false, "1시간 전"),
        ChatMessage(4, 0, MessageType.PROPOSAL, "다음 주 월요일부터 가능합니다", "", 0, null, false, "1시간 전")
    )
    ChattingScreenContent(
        title = "김아티스트",
        messages = dummyMessages,
        profileImg = "",
        myUserId = 0,
        partnerName = "fff"
    )
}
