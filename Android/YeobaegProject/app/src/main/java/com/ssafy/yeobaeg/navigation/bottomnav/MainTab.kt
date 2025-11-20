package com.ssafy.yeobaeg.navigation.bottomnav

import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.navigation.BottomNavScreen

enum class MainTab(
    val iconResId: Int,
    internal val contentDescription: String,
    val label: String,
    val route: BottomNavScreen
) {
    HOME(
        iconResId = R.drawable.ic_home,
        contentDescription = "home Icon",
        label = "홈",
        route = BottomNavScreen.Home,
    ),
    CAFE(
        iconResId = R.drawable.ic_cafe,
        contentDescription = "cafe Icon",
        label = "카페",
        route = BottomNavScreen.Cafe,
    ),
    ARTWORK(
        iconResId = R.drawable.ic_artwork,
        contentDescription = "artwork Icon",
        label = "작품",
        route = BottomNavScreen.Artwork,
    ),
    CHAT(
        iconResId = R.drawable.ic_chat,
        contentDescription = "chat Icon",
        label = "채팅",
        route = BottomNavScreen.Chat,
    ),
    PROFILE(
        iconResId = R.drawable.ic_profile,
        contentDescription = "profile Icon",
        label = "프로필",
        route = BottomNavScreen.Profile(null),
    )
}