package com.ssafy.yeobaeg.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomInfo
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomSummary
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.util.DateFormatter
import com.ssafy.yeobaeg.presentation.main.MainViewModel
import com.ssafy.yeobaeg.ui.theme.*

@Composable
fun ChatListScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // ★ MainViewModel 가져오기
    val mainViewModel: MainViewModel = hiltViewModel()

    // 최초 로딩
    LaunchedEffect(Unit) {
        viewModel.loadChatRooms()
    }

    // 전역 메시지 들어오면 채팅 리스트 새로고침
    LaunchedEffect(Unit) {
        mainViewModel.chatListRefreshFlow.collect {
            viewModel.loadChatRooms()
        }
    }
    DisposableEffect(Unit) {
        mainViewModel.setOnChatListScreen(true)

        onDispose {
            mainViewModel.setOnChatListScreen(false)
        }
    }
    ChatListScreenContent(
        uiState = uiState,
        onChatClick = { viewModel.navigateToChatRoom(it) }
    )
}

private fun getDisplayMessage(type: String?, message: String?): String {
    return when (type) {
        "TEXT" -> message ?: ""
        "IMAGE" -> "사진을 보냈습니다"
        "AGREED" -> "매칭을 수락했습니다"
        "PROPOSAL" -> "매칭 제안을 보냈습니다"
        "REJECTED" -> "매칭이 거절 되었습니다."
        "STARTED" -> "전시가 시작되었습니다."
        "ENDED" -> "전시가 종료되었습니다."
        else -> ""
    }
}

/**
 * 실제 화면과 프리뷰 둘 다에서 재사용 가능한 Content 분리
 */
@Composable
fun ChatListScreenContent(
    uiState: ChatUiState,
    onChatClick: (Long) -> Unit = {}
) {


    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "채팅",
                onBackClick = {},
                showBackButton = false
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        when {
            uiState.chatRooms.isEmpty() && uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "오류 발생: ${uiState.errorMessage}")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(YeobaegWhite)
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(
                        uiState.chatRooms,
                        key = { _, item -> item.chatRoomId }) { index, chat ->
                        ChatListItem(
                            chat = chat,
                            onClick = { onChatClick(chat.chatRoomId) }
                        )

                        if (index < uiState.chatRooms.lastIndex) {
                            HorizontalDivider(
                                color = YeobaegLightGray,
                                thickness = 0.7.dp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatRoomInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(YeobaegWhite)
            .padding(horizontal = 15.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = chat.partnerImageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(1.dp, YeobaegLightGray, CircleShape),
            contentScale = ContentScale.Crop,
            fallback = painterResource(R.drawable.profile),

        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = chat.partnerName,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Medium,
                color = YeobaegBlack,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = getDisplayMessage(chat.lastMessageType, chat.lastMessage),
                fontFamily = jansilFont,
                fontWeight = FontWeight.Light,
                color = YeobaegBlack,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(45.dp)
        ) {
            Text(
                text = DateFormatter.toTimeAgo(chat.lastMessageAt.toString()),
                fontFamily = jansilFont,
                fontWeight = FontWeight.Light,
                color = YeobaegTextGray,
                fontSize = 13.sp
            )

//            읽음처리 기능 일단 보류
            if (chat.unreadCount != null && chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(YeobaegBrown, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chat.unreadCount.toString(),
                        color = YeobaegWhite,
                        fontFamily = jansilFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    val dummyUiState = ChatUiState(
        chatRooms = listOf(
            ChatRoomInfo(
                chatRoomId = 1,
                partnerId = 101,
                partnerName = "김아티스트",
                partnerImageUrl = "",
                partnerRole = UserRole.ARTIST.toString(),
                unreadCount = 2,
                lastMessage = "작품 전시에 대해 논의하고 싶습니다",
                lastMessageType = "TEXT",
                lastMessageAt = "2분 전"
            ),
            ChatRoomInfo(
                chatRoomId = 2,
                partnerId = 102,
                partnerName = "Brew & Canvas",
                partnerImageUrl = "",
                partnerRole = UserRole.CAFE_OWNER.toString(),
                unreadCount = 0,
                lastMessage = "전시 공간이 준비되었습니다",
                lastMessageType = "TEXT",
                lastMessageAt = "1시간 전"
            )
        )
    )

    ChatListScreenContent(uiState = dummyUiState)
}
