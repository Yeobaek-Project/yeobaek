package com.ssafy.yeobaeg.presentation.profile.artist

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.profile.ArtistInfo
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import com.ssafy.yeobaeg.presentation.common.component.YeobaegGrid
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ArtistProfileScreen(
    userId: Long? = null,
    artistViewModel: ArtistProfileViewModel = hiltViewModel()
) {

    val uiState by artistViewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        artistViewModel.loadArtistProfile(userId)
        artistViewModel.loadArtworks(userId)
    }

    Scaffold(
        containerColor = YeobaegWhite,
        topBar = {
            YeobaegTopAppBar(
                title = "프로필",
                onBackClick = {},
                actionIcon = null,
                onActionClick = {},
                showBackButton = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegWhite)
                .padding(top = padding.calculateTopPadding())
        ) {
            ProfileHeader(
                userInfo = uiState.userInfo,
                artistInfo = uiState.artistInfo,
                onEditClick = { artistViewModel.navigateToEditProfile(uiState.userInfo,
                    uiState.artistInfo?.introduction
                ) },
                isMyProfile = (userId == null)
            )

            ProfileTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                isMyProfile = (userId == null)
            )

            when (selectedTab) {
                0 -> {
                    YeobaegGrid(
                        artworks = uiState.artworks,
                        isLoading = uiState.isLoading,
                        hasNext = uiState.pagination?.hasNext ?: true,
                        onLoadMore = { artistViewModel.loadNextArtworks(userId) },
                    )
                }

                1 -> {
                    if (userId == null) {
                        // 내 프로필인 경우: 설정 화면 표시
                        ProfileSettingsSection(
                            artistInfo = uiState.artistInfo,
                            bookmarkCount = uiState.bookmarkCount,
                            onBookmarkClick = artistViewModel::navigateToBookmark,
                            onReviewClick = artistViewModel::navigateToReviewManagement,
                            onLogoutClick = artistViewModel::logout
                        )
                    } else {
                        // 다른 사람 프로필인 경우: 리뷰 화면
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(YeobaegWhite)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    userInfo: UserInfo,
    artistInfo: ArtistInfo?,
    onEditClick: () -> Unit,
    isMyProfile: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YeobaegWhite)
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 프로필 이미지
            AsyncImage(
                model = userInfo.profileImageUrl?: R.drawable.profile,
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(1.dp, YeobaegLightGray, CircleShape)
            )

            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                //닉네임, 프로필 편집 버튼
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        userInfo.nickname,
                        fontFamily = jansilFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(Modifier.weight(1f))
                    if (isMyProfile) {
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
                Spacer(Modifier.height(17.dp))
                ProfileStatsRow(
                    artworkCount = artistInfo?.artworkCount ?: 0,
                    exhibitingCount = artistInfo?.exhibitingCount
                        ?: 0
                )
            }
        }
        Spacer(Modifier.height(4.dp))
//        Text("추상화, 현대미술", color = YeobaegTextGray, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(Modifier.height(12.dp))


        Spacer(Modifier.height(12.dp))
        Text(
            artistInfo?.introduction ?: "",
            color = YeobaegTextGray,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ProfileStatsRow(
    artworkCount: Int,
    exhibitingCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProfileStatItem(artworkCount, "작품 수")
        ProfileStatItem(exhibitingCount, "전시 중")
        ProfileStatItem(exhibitingCount, "전시 횟수")
    }
}

@Composable
fun ProfileStatItem(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = YeobaegBlack)
        Text(label, fontSize = 13.sp, color = YeobaegTextGray)
    }
}


@Composable
fun ProfileTabs(selectedTab: Int, onTabSelected: (Int) -> Unit, isMyProfile: Boolean) {
    val tabs = if (isMyProfile) {
        listOf(
            R.drawable.ic_grid,
            R.drawable.ic_option_list    // 설정 탭 (내 프로필 전용)
        )
    } else {
        listOf(
            R.drawable.ic_grid,
            R.drawable.ic_star        // 다른 사람 프로필일 땐 리뷰 아이콘
        )
    }
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = YeobaegWhite,
        contentColor = YeobaegBrown,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .height(3.dp)
                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)),
                color = YeobaegBrown
            )
        }
    ) {
        tabs.forEachIndexed { index, iconRes ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = if (selectedTab == index) YeobaegBrown else YeobaegTextGray,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun ProfileSettingsSection(
    artistInfo: ArtistInfo?,
    bookmarkCount: Int,
    onBookmarkClick: () -> Unit,
    onReviewClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val reviewCount = artistInfo?.ratingStats?.totalReviews ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        SettingItem(
            "좋아요 한 목록",
            if (bookmarkCount > 0) bookmarkCount.toString() else null,
            R.drawable.ic_heart,
            onBookmarkClick
        )
        SettingItem(
            "리뷰 관리",
            if (reviewCount > 0) reviewCount.toString() else null,
            R.drawable.ic_star,
            onReviewClick
        )
        SettingItem("알림 설정", null, R.drawable.ic_notify, {})

        Spacer(Modifier.weight(1f))
        LogoutButton(onClick=onLogoutClick)
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
        border = BorderStroke(1.dp, Color(0xFFFFE0E0))
    ) {
        Text(
            text = "로그아웃",
            color = Color(0xFFFF6B6B),
            fontSize = 16.sp
        )
    }
}


@Composable
fun SettingItem(title: String, badge: String?, iconRes: Int, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = YeobaegBrown
        )
        Spacer(Modifier.width(12.dp))
        Text(title, fontFamily = jansilFont, color = YeobaegBlack, fontSize = 15.sp)
        Spacer(Modifier.weight(1f))
        if (badge != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF0D9))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(badge, color = YeobaegBrown, fontSize = 13.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ArtistProfileScreen()
}
