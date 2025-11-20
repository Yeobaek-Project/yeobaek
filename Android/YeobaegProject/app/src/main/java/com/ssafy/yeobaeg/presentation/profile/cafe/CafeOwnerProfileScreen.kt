package com.ssafy.yeobaeg.presentation.profile.cafe

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.profile.CafeInfo
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun CafeOwnerProfileScreen(
    cafeViewModel: CafeOwnerProfileViewModel = hiltViewModel(),
) {
    val uiState by cafeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        cafeViewModel.loadCafeProfile()
    }

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "프로필",
                onBackClick = {},
                showBackButton = false,
                actionIcon = null,
                onActionClick = null
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(YeobaegWhite)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.errorMessage != null -> {
                    ErrorContent(
                        message = uiState.errorMessage!!,
                        onRetry = cafeViewModel::refresh,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    CafeOwnerProfileContent(
                        userInfo = uiState.userInfo,
                        cafeInfo = uiState.cafeInfo,
                        onEditClick = { cafeViewModel.navigateToEditProfile(uiState.userInfo,null) },
                        onBookmarkClick = cafeViewModel::navigateToBookmark,
                        onReviewClick = cafeViewModel::navigateToReviewManagement,
                        onLogoutClick = cafeViewModel::logout
                    )
                }
            }
        }
    }
}

@Composable
fun CafeOwnerProfileContent(
    userInfo: UserInfo,
    cafeInfo: CafeInfo?,
    onEditClick: () -> Unit,
    onReviewClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 25.dp, vertical = 25.dp)
    ) {
        // 프로필 부분
        ProfileSection(
            userInfo = userInfo,
            onEditClick = onEditClick
        )

        Spacer(modifier = Modifier.height(40.dp))

        if(cafeInfo == null) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center) {
                Text(
                    text = "매장 정보가 없습니다.",
                    fontSize = 16.sp,
                    color = YeobaegTextGray
                )
            }
            return@Column
        }

        // 매장 정보 부분
        StoreInfoSection(cafeInfo = cafeInfo)

        Spacer(modifier = Modifier.height(32.dp))

        // 설정 메뉴 부분
        MenuListSection(
            onEditClick = onBookmarkClick,
            onReviewClick = onReviewClick,
            cafeInfo = cafeInfo
        )
        Spacer(modifier = Modifier.height(32.dp))
        // 로그아웃 버튼
        LogoutButton(onClick = onLogoutClick)
    }
}

@Composable
fun ProfileSection(
    userInfo: UserInfo,
    onEditClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지
        AsyncImage(
            model = userInfo.profileImageUrl,
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(50.dp)),
            contentScale = ContentScale.Crop,
            fallback = painterResource(R.drawable.profile)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Name and Email
        Column {
            Text(
                text = userInfo.nickname,
                fontSize = 22.sp,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Bold,
                color = YeobaegBlack
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = userInfo.email,
                fontSize = 14.sp,
                color = YeobaegTextGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Edit Profile Button
            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "프로필 편집",
                    fontSize = 13.sp,
                    color = YeobaegBlack
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun StoreInfoSection(cafeInfo: CafeInfo) {
    Column {
        // Title
        Text(
            text = "매장 정보",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Store Details
        StoreDetailItem(
            title = cafeInfo.cafeName,
            subtitle = cafeInfo.address,
            icon = R.drawable.ic_storeedit
        )

        Spacer(modifier = Modifier.height(16.dp))

        StoreDetailItem(
            title = "운영시간",
            subtitle = "${cafeInfo.businessHoursStart} - ${cafeInfo.businessHoursEnd}",
            icon = R.drawable.ic_time
        )

        Spacer(modifier = Modifier.height(16.dp))

        StoreDetailItem(
            title = "리뷰 ${cafeInfo.ratingStats.totalReviews}개",
            subtitle = "평균 평점: ${
                String.format(
                    "%.1f",
                    cafeInfo.ratingStats.averageRating
                )
            }",
            icon = R.drawable.ic_star
        )
    }
}

@Composable
fun MenuListSection(
    onEditClick: () -> Unit,
    onReviewClick: () -> Unit,
    cafeInfo: CafeInfo
) {
    Column {
        MenuListItem(
            title = "좋아요 한 목록",
            badge = null,
            icon = R.drawable.ic_heart,
            onClick = onEditClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = Color(0xFFE0E0E0),
            thickness = 1.dp
        )

        MenuListItem(
            title = "매장 정보 수정",
            badge = null,
            icon = R.drawable.ic_storeedit
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = Color(0xFFE0E0E0),
            thickness = 1.dp
        )
        MenuListItem(
            title = "리뷰 관리",
            badge = cafeInfo.ratingStats.totalReviews,
            icon = R.drawable.ic_star,
            onClick = onReviewClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = Color(0xFFE0E0E0),
            thickness = 1.dp
        )
        MenuListItem(
            title = "알림 설정",
            badge = null,
            icon = R.drawable.ic_time
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = {onClick()},
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFFF6B6B)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0E0))
    ) {
        Text(
            text = "로그아웃",
            color = Color(0xFFFF6B6B),
            fontSize = 16.sp
        )
    }
}

@Composable
fun StoreDetailItem(
    title: String,
    subtitle: String,
    icon: Int
) {
    Column {
        Row {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "매장 수정",
                modifier = Modifier.size(20.dp),
                tint = YeobaegBrown
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = YeobaegBlack
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = YeobaegTextGray
                )
            }

        }

    }
}

@Composable
fun MenuListItem(
    title: String,
    badge: Int?,
    icon: Int,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "설정 아이콘",
            modifier = Modifier.size(20.dp),
            tint = YeobaegGray
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black,
        )

        Spacer(modifier = Modifier.width(10.dp))

        if (badge != null && badge > 0) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badge.toString(),
                    fontSize = 13.sp,
                    color = Color(0xFF754106),
                    fontWeight = FontWeight.Medium
                )
            }

        }
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = "화살표",
            modifier = Modifier.size(30.dp),
            tint = YeobaegGray
        )
    }
}


@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Red,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onRetry) {
            Text("다시 시도")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CafeOwnerProfileScreenPreview() {
    CafeOwnerProfileScreen()
}


