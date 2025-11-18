package com.ssafy.yeobaeg.presentation.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import com.ssafy.yeobaeg.domain.model.chat.MessageType
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionInfo
import com.ssafy.yeobaeg.presentation.chat.component.BasicSystemMessage
import com.ssafy.yeobaeg.presentation.common.component.SnackbarType
import com.ssafy.yeobaeg.presentation.common.component.YeobaegSnackbarHost
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.component.showYeobaegSnackbar
import com.ssafy.yeobaeg.presentation.common.util.DateFormatter
import com.ssafy.yeobaeg.presentation.common.util.clearFocusOnTap
import com.ssafy.yeobaeg.presentation.main.alignToSafeDrawing
import com.ssafy.yeobaeg.ui.theme.YeobaegBackground
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

private const val TAG = "ChattingScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChattingScreen(
    chatRoomId: Long,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val myUserId = viewModel.myUserId.collectAsState().value
    var inputText by remember { mutableStateOf("") }
    var showMatchingInfoDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage = uiState.errorMessage
    var showReviewDialog by remember { mutableStateOf(false) }

    // 이미지 선택 런처
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.sendImageMessage(chatRoomId, it, context)
        }
    }
    LaunchedEffect(chatRoomId) {
        viewModel.loadChatMessages(chatRoomId)
        viewModel.subscribeToChatRoom(chatRoomId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.unsubscribeFromChatRoom(chatRoomId)
        }
    }
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            snackbarHostState.showYeobaegSnackbar(
                message = errorMessage,
                type = SnackbarType.ERROR
            )
            viewModel.clearErrorMessage()
        }
    }

    ChattingScreenContent(
        title = uiState.chatRoomMessages.chatRoom.partnerName,
        profileImg = uiState.chatRoomMessages.chatRoom.partnerImageUrl,
        messages = uiState.chatRoomMessages.messages,
        partnerName = uiState.chatRoomMessages.chatRoom.partnerName,
        exhibitionInfo = uiState.exhibitionInfo,
        isLoading = uiState.isLoading,
        isLoadingMore = uiState.isLoadingMore,
        hasNext = uiState.chatRoomMessages.pagination.hasNext,
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
        onImageClick = {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onProposeAccept = { exhibitionId ->
            viewModel.loadExhibitionInfo(exhibitionId)
            showMatchingInfoDialog = true
        },
        onReject = { exhibitionId ->
            viewModel.rejectExhibition(exhibitionId)
        },
        onLoadMore = {
            viewModel.loadMoreMessages(chatRoomId)
        },
        myUserId = myUserId,
        snackbarHostState = snackbarHostState,
        onReviewClickShow = { showReviewDialog = true }
    )
    if (showMatchingInfoDialog) {
        MatchingInfoDialog(
            info = uiState.exhibitionInfo,
            onDismiss = { showMatchingInfoDialog = false },
            onConfirm = {
                viewModel.agreeExhibition(uiState.exhibitionInfo.exhibitionId)
                showMatchingInfoDialog = false
            }
        )
    }
    if (showReviewDialog) {
        ReviewDialog(
            exhibitionId = uiState.exhibitionInfo.exhibitionId,
            targetUserId = uiState.chatRoomMessages.chatRoom.partnerId,
            viewModel = viewModel,
            onDismiss = { showReviewDialog = false }
        )
    }

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
    exhibitionInfo: ExhibitionInfo,
    messages: List<ChatMessage> = emptyList(),
    isLoading: Boolean = false,
    isLoadingMore: Boolean = false,
    hasNext: Boolean = false,
    inputText: String = "",
    onInputChange: (String) -> Unit = {},
    onSendClick: (String) -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    onMatchingClick: () -> Unit = {},
    onImageClick: () -> Unit = {},
    onProposeAccept: (Long) -> Unit,
    onReject: ((Long) -> Unit)? = null,
    onLoadMore: () -> Unit = {},
    onReviewClickShow: () -> Unit,
    snackbarHostState: SnackbarHostState,
    myUserId: Long?
) {
    val listState = rememberLazyListState()

    // 키보드가 올라올 때와 새 메시지가 추가될 때 자동 스크롤
    // messages.size 대신 최신 메시지 ID를 추적하여 페이징 시 스크롤 방지
    val latestMessageId = messages.firstOrNull()?.messageId
    LaunchedEffect(latestMessageId) {
        if (latestMessageId != null) {
            delay(100)
            listState.animateScrollToItem(0)
        }
    }

    // 무한 스크롤: 마지막 아이템에 도달했을 때 추가 로드
    LaunchedEffect(hasNext, isLoadingMore) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItems = layoutInfo.totalItemsCount
            Triple(lastIndex, totalItems, layoutInfo.visibleItemsInfo.lastOrNull()?.key)
        }
            .distinctUntilChanged()
            .collect { (lastVisibleItemIndex, totalItems, _) ->
                // reverseLayout이므로 마지막 인덱스(가장 오래된 메시지)가 보일 때 로드
                val shouldLoad = lastVisibleItemIndex != null &&
                        totalItems > 0 &&
                        lastVisibleItemIndex >= totalItems - 5 &&
                        hasNext &&
                        !isLoadingMore

                android.util.Log.d(
                    TAG,
                    "Scroll Check - lastIndex: $lastVisibleItemIndex, total: $totalItems, hasNext: $hasNext, isLoading: $isLoadingMore, shouldLoad: $shouldLoad"
                )

                if (shouldLoad) {
                    android.util.Log.d(TAG, "Loading more messages...")
                    onLoadMore()
                }
            }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            YeobaegTopAppBar(
                title = title,
                onBackClick = onBackClick ?: {},
                showBackButton = true
            )
        },
        snackbarHost = {
            YeobaegSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            ChatInputBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YeobaegWhite)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .alignToSafeDrawing(),
                value = inputText,
                onValueChange = onInputChange,
                onSendClick = onSendClick,
                onMatchingClick = onMatchingClick,
                onImageClick = onImageClick
            )
        },
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

                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .clearFocusOnTap()
                        .background(YeobaegWhite)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    reverseLayout = true
                ) {
                    // 로딩 인디케이터 (reverseLayout이므로 실제로는 상단에 표시됨)
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }

                    items(
                        items = messages,
                        key = { msg -> msg.messageId }
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
                                val isMy = myUserId != null && msg.senderId == myUserId
                                ImageMessageBubble(
                                    message = msg,
                                    isMyMessage = isMy,
                                    profileImg = profileImg
                                )
                            }

                            // 전시 제안 메시지
                            MessageType.PROPOSAL -> {
                                ProposalMessageBubble(
                                    msg,
                                    partnerName = partnerName,
                                    myId = myUserId,
                                    onAccept = {
                                        msg.exhibitionId?.let(onProposeAccept)
                                    },
                                    onReject = {
                                        msg.exhibitionId?.let { exhibitionId ->
                                            onReject?.invoke(exhibitionId)
                                        }
                                    }
                                )
                            }

                            // 전시 수락 / 거절 / 취소 / 시작 / 종료 (시스템 메시지)
                            MessageType.AGREED,
                            MessageType.REJECTED,
                            MessageType.CANCELLED -> {
                                BasicSystemMessage(
                                    message = msg,
                                    isMy = (myUserId == msg.senderId),
                                    partnerName = partnerName
                                )
                            }

                            MessageType.STARTED -> {
                                val info = exhibitionInfo

                                ExhibitionEndedMessage(
                                    type = "STARTED",
                                    info = info,
                                    onReviewClick = { onReviewClickShow() },
                                )
                            }

                            MessageType.ENDED -> {
                                val info = exhibitionInfo
                                ExhibitionEndedMessage(
                                    type = "ENDED",
                                    info = info,
                                    onReviewClick = { onReviewClickShow() },
                                )
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
            model = profileImg ?: R.drawable.profile,
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
fun ImageMessageBubble(
    message: ChatMessage,
    isMyMessage: Boolean,
    profileImg: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // 상대방 메시지일 때 프로필 이미지
        if (!isMyMessage) {
            AsyncImage(
                model = profileImg ?: R.drawable.profile,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .border(0.5.dp, YeobaegLightGray, CircleShape)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start
        ) {
            // 이미지
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (isMyMessage) 12.dp else 4.dp,
                    topEnd = 12.dp,
                    bottomStart = 12.dp,
                    bottomEnd = if (isMyMessage) 4.dp else 12.dp
                ),
                shadowElevation = if (isMyMessage) 0.dp else 1.dp,
                modifier = Modifier.widthIn(max = 240.dp)
            ) {
                AsyncImage(
                    model = message.imageUrl,
                    contentDescription = "전송된 이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 300.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = if (isMyMessage) 12.dp else 4.dp,
                                topEnd = 12.dp,
                                bottomStart = 12.dp,
                                bottomEnd = if (isMyMessage) 4.dp else 12.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            // 시간
            Text(
                text = DateFormatter.toTimeAgo(message.createdAt),
                color = YeobaegTextGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(
                    top = 4.dp,
                    start = if (isMyMessage) 0.dp else 4.dp,
                    end = if (isMyMessage) 4.dp else 0.dp
                )
            )
        }
    }
}

//제안 요청 시스템 메시지
@Composable
fun ProposalMessageBubble(
    message: ChatMessage,
    myId: Long?,
    partnerName: String = "상대방",
    onAccept: ((Long?) -> Unit)? = null,
    onReject: ((Long?) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(350.dp),
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
                    text = "매칭 요청 ❤️",
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
                                .clickable { onReject?.invoke(message.exhibitionId) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "거절",
                                fontFamily = jansilFont,
                                fontSize = 14.sp,
                                color = YeobaegBlack
                            )
                        }

                        // 요청 내용 확인 버튼
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(YeobaegBrown)
                                .clickable { onAccept?.invoke(message.exhibitionId) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "요청 내용 확인",
                                fontFamily = jansilFont,
                                fontSize = 14.sp,
                                color = YeobaegWhite
                            )
                        }
                    }
                }
                Text(
                    text = DateFormatter.toTimeAgo(message.createdAt),
                    fontSize = 11.sp,
                    color = YeobaegTextGray
                )

            }
        }
    }
}

@Composable
fun MatchingStatusMessageBubble(
    message: ChatMessage,
    partnerName: String,
    myId: Long?
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "매칭 상태 알림",
                fontFamily = jansilFont,
                fontSize = 16.sp,
                color = YeobaegBlack
            )

            val isMy = message.senderId == myId

            val textBody = when (message.messageType) {
                MessageType.AGREED ->
                    "매칭이 수락되었습니다!"


                MessageType.REJECTED ->
                    if (isMy) "${partnerName}님의 제안을 거절했습니다."
                    else "당신의 제안이 거절되었습니다."

                MessageType.CANCELLED ->
                    "전시가 취소되었습니다."

                else -> ""
            }

            Text(
                text = textBody,
                fontFamily = jansilFont,
                fontSize = 14.sp,
                color = YeobaegBlack
            )

            Text(
                text = DateFormatter.toTimeAgo(message.createdAt),
                color = YeobaegTextGray,
                fontSize = 11.sp
            )
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
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onMatchingClick: () -> Unit = {},
    onImageClick: () -> Unit = {},

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
                        imageVector = Icons.AutoMirrored.Filled.Send,
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
                    DialogOption("사진", Color(0xFFE3F2FD)) {
                        showDialog = false
                        onImageClick()
                    }
                    DialogOption("매칭 요청", Color(0xFFFFF8E1)) {
                        showDialog = false
                        onMatchingClick()
                    }
                }
            },
            containerColor = YeobaegWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// 사진, 매칭 버튼
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

@Composable
fun ExhibitionEndedMessage(
    type: String,
    info: ExhibitionInfo,
    onReviewClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
//            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        color = Color.White
    ) {
        Column {
            // 상단 베이지 바
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFD2C1A5))
                    .padding(15.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_end), // 종료 아이콘
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (type) {
                            "STARTED" -> "전시 시작"
                            "ENDED" -> "전시 종료"
                            else -> {
                                ""
                            }
                        },
                        fontFamily = jansilFont,
                        fontSize = 15.sp,
                        color = Color.White
                    )

                }
            }
            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {


                // 작가
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = null,
                        tint = YeobaegBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "작가: ${info.artwork.artistName}",
                        fontFamily = jansilFont,
                        fontSize = 14.sp,
                        color = YeobaegBlack
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = null,
                        tint = YeobaegBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "카페: ${info.cafe.cafeName}",
                        fontFamily = jansilFont,
                        fontSize = 14.sp,
                        color = YeobaegBlack
                    )
                }
                // 벽
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pin),
                        contentDescription = null,
                        tint = YeobaegBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "전시 공간: ${info.slot.wallName}",
                        fontFamily = jansilFont,
                        fontSize = 14.sp,
                        color = YeobaegBlack
                    )
                }

                // 기간
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cal),
                        contentDescription = null,
                        tint = YeobaegBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "기간: ${info.startDate} - ${info.endDate}",
                        fontFamily = jansilFont,
                        fontSize = 14.sp,
                        color = YeobaegBlack
                    )
                }

                // 리뷰하기 버튼
                if (type == "ENDED") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF6A4E2F))
                            .clickable { onReviewClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(
                                painter = painterResource(id = R.drawable.ic_star),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = "리뷰하기",
                                fontFamily = jansilFont,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
                // 하단 문구
                Text(
                    text = when (type) {
                        "STARTED" -> "전시가 시작되었습니다! ✨"
                        "ENDED" -> "전시가 성공적으로 종료되었습니다.!"
                        else -> {
                            ""
                        }
                    },
                    fontFamily = jansilFont,
                    fontSize = 14.sp,
                    color = YeobaegTextGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

        }
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
        exhibitionInfo = ExhibitionInfo(),
        partnerName = "fff",
        isLoadingMore = false,
        hasNext = false,
        onProposeAccept = {},
        snackbarHostState = SnackbarHostState(),
        onReviewClickShow = {}
    )
}