package com.ssafy.yeobaeg.presentation.main

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.presentation.common.component.YeobaegSnackbarHost
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.bottomnav.MainBottomBar
import com.ssafy.yeobaeg.navigation.bottomnav.MainTab
import com.ssafy.yeobaeg.presentation.arcore.edit.WallEditScreen
import com.ssafy.yeobaeg.presentation.arcore.horizontal.HorizontalMeasureCamera
import com.ssafy.yeobaeg.presentation.artwork.ArtExploreScreen
import com.ssafy.yeobaeg.presentation.artwork.detail.ArtworkDetailScreen
import com.ssafy.yeobaeg.presentation.chat.ChatListScreen
import com.ssafy.yeobaeg.presentation.chat.ChattingScreen
import com.ssafy.yeobaeg.presentation.home.artist.ArtistHomeScreen
import com.ssafy.yeobaeg.presentation.cafe.CafeDetailScreen
import com.ssafy.yeobaeg.presentation.cafe.CafeMapScreen
import com.ssafy.yeobaeg.presentation.chat.matching.MatchingArtworkScreen
import com.ssafy.yeobaeg.presentation.chat.matching.MatchingDateScreen
import com.ssafy.yeobaeg.presentation.chat.matching.MatchingFeeScreen
import com.ssafy.yeobaeg.presentation.chat.matching.MatchingNoteScreen
import com.ssafy.yeobaeg.presentation.chat.matching.MatchingSpaceScreen
import com.ssafy.yeobaeg.presentation.chat.matching.MatchingViewModel
import com.ssafy.yeobaeg.presentation.exhibition.ExhibitionCalendarScreen
import com.ssafy.yeobaeg.presentation.home.GeneralHomeScreen
import com.ssafy.yeobaeg.presentation.home.artist.ArtworkRegisterScreen
import com.ssafy.yeobaeg.presentation.home.artist.matching.ArtistHomeMatchingScreen
import com.ssafy.yeobaeg.presentation.home.cafeowner.CafeOwnerHomeScreen
import com.ssafy.yeobaeg.presentation.home.cafeowner.editslot.EditDisplaySlotScreen
import com.ssafy.yeobaeg.presentation.home.cafeowner.matching.CafeOwnerMatchingScreen
import com.ssafy.yeobaeg.presentation.home.cafeowner.register.RegisterCafeScreen
import com.ssafy.yeobaeg.presentation.login.LoginScreen
import com.ssafy.yeobaeg.presentation.onboarding.OnboardingScreen
import com.ssafy.yeobaeg.presentation.profile.BookmarkScreen
import com.ssafy.yeobaeg.presentation.profile.ProfileEditScreen
import com.ssafy.yeobaeg.presentation.profile.artist.ArtistProfileScreen
import com.ssafy.yeobaeg.presentation.profile.cafe.CafeOwnerProfileScreen
import com.ssafy.yeobaeg.presentation.review.ReviewManagementScreen
import com.ssafy.yeobaeg.presentation.profile.general.GeneralProfileScreen
import com.ssafy.yeobaeg.presentation.signup.SignUpScreen
import com.ssafy.yeobaeg.presentation.splash.SplashScreen
import kotlinx.coroutines.launch

val LocalUserRole = compositionLocalOf<UserRole?> { null }

@Composable
fun NavGraph(
    navBackStack: NavBackStack,
    snackbarHostState: SnackbarHostState,
    onTabSelected: (MainTab) -> Unit = {},
) {
    var lastBackPressedTime by remember { mutableLongStateOf(0L) }
    val context = LocalContext.current
    val matchingViewModel: MatchingViewModel = hiltViewModel(context as MainActivity)

    BackHandler {
        val now = System.currentTimeMillis()
        val current = navBackStack.lastOrNull()
        Log.d("nav graph", "BackHandler invoked. current = $current, stackSize = ${navBackStack.size}")

        when (current) {
            is BottomNavScreen.Home -> {
                if (now - lastBackPressedTime <= 2000L) {
                    (context as? Activity)?.finish()
                } else {
                    lastBackPressedTime = now
                    // Toast 또는 Snackbar 중 선호하는 걸 사용
                    Toast.makeText(context, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                }
            }

            is BottomNavScreen -> {
                Log.d("nav graph", "Bottom tab (not home) - navigate to Home")
                // 현재 탭 제거 후 Home 추가
                navBackStack.removeLastOrNull()
                navBackStack.add(BottomNavScreen.Home)
            }

            else -> {
                Log.d("nav graph", "FullScreen or other - pop or finish, stackSize=${navBackStack.size}")
                if (navBackStack.size > 1) {
                    navBackStack.removeLastOrNull()
                } else {
                    (context as? Activity)?.finish()
                }
            }
        }
    }


    Scaffold(
        bottomBar = {
            if (navBackStack.last() is BottomNavScreen)
                MainBottomBar(
                    tabs = MainTab.entries.toList(),
                    currentTab = mapTab(navBackStack.lastOrNull()),
                    onTabSelected = onTabSelected,
                )
        },
        snackbarHost = {
            YeobaegSnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        NavDisplay(
            backStack = navBackStack,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding),
            entryDecorators = listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when (key) {
                    is FullScreen.Splash -> NavEntry(key) {
                        SplashScreen(
                            onNavigateToOnboarding = {
                                navBackStack.clear()
                                navBackStack.add(FullScreen.Onboarding)
                            }
                        )
                    }

                    is FullScreen.Onboarding -> NavEntry(key) {
                        OnboardingScreen(
                            onNavigateToLogin = {
                                navBackStack.clear()
                                navBackStack.add(FullScreen.Login)
                            }
                        )
                    }

                    is FullScreen.Login -> NavEntry(key) {
                        LoginScreen()
                    }

                    is FullScreen.SignUp -> NavEntry(key) {
                        SignUpScreen()
                    }

                    is BottomNavScreen.Home -> NavEntry(key) {
                        when (LocalUserRole.current) {
                            UserRole.CAFE_OWNER -> CafeOwnerHomeScreen()
                            UserRole.ARTIST -> ArtistHomeScreen()
                            UserRole.GENERAL, null -> GeneralHomeScreen()
                        }
                    }

                    is BottomNavScreen.Cafe -> NavEntry(key) {
                        CafeMapScreen()
                    }

                    is BottomNavScreen.Artwork -> NavEntry(key) {
                        // Artwork Screen Composable
                        ArtExploreScreen()
                    }

                    is FullScreen.WallEdit -> NavEntry(key) {
                        WallEditScreen(key.displayWalls)
                    }

                    is BottomNavScreen.Profile -> NavEntry(key) {
                        Log.d("NavGraph", "Current UserRole: ${LocalUserRole.current?.displayName}")
                        when (LocalUserRole.current) {
                            UserRole.CAFE_OWNER -> CafeOwnerProfileScreen()
                            UserRole.ARTIST -> ArtistProfileScreen(key.userId)
                            UserRole.GENERAL, null -> GeneralProfileScreen()
                        }

                    }

                    is BottomNavScreen.Chat -> NavEntry(key) {
                        ChatListScreen()
                    }

                    is FullScreen.ProfileEdit -> NavEntry(key) {
                        ProfileEditScreen(key.userInfo, key.introduction)
                    }

                    is FullScreen.SpaceScan -> NavEntry(key) {
                        HorizontalMeasureCamera()
                    }

                    is FullScreen.RegisterCafe -> NavEntry(key) {
                        // Cafe Registration Screen Composable
                        RegisterCafeScreen()
                    }

                    is FullScreen.RegisterArtwork -> NavEntry(key) {
                        ArtworkRegisterScreen()
                    }

                    is FullScreen.ArtworkDetail -> NavEntry(key) {
                        ArtworkDetailScreen(artworkId = key.artworkId)
                    }

                    is FullScreen.CafeDetail -> NavEntry(key) {
                        CafeDetailScreen(
                            cafeId = key.cafeId,
                        )
                    }

                    is FullScreen.BookmarkList -> NavEntry(key) {
                        BookmarkScreen()
                    }

                    is FullScreen.ReviewManagementScreen -> NavEntry(key) {
                        ReviewManagementScreen()
                    }

                    is FullScreen.EditDisplaySlotScreen -> NavEntry(key) {
                        EditDisplaySlotScreen(
                            key.cafeId,
                            key.slotId,
                            key.commissionRate,
                            key.requirements
                        )
                    }
                    is FullScreen.ChattingScreen-> NavEntry(key){
                        ChattingScreen(key.chatRoomId)
                    }

                    is FullScreen.ArtistProfile -> NavEntry(key) {
                        ArtistProfileScreen(key.artistId)
                    }

                    is FullScreen.ArtistHomeMatching -> NavEntry(key) {
                        ArtistHomeMatchingScreen()
                    }

                    is FullScreen.ExhibitionCalendar -> NavEntry(key) {
                        ExhibitionCalendarScreen(
                            slotId = key.slotId,
                            slotName = key.slotName,
                            width = key.width,
                            height = key.height,
                            commission = key.commission,
                            requirements = key.requirements
                        )
                    }

                    is FullScreen.CafeOwnerMatching -> NavEntry(key) {
                        CafeOwnerMatchingScreen(key.slots)
                    }

                    is FullScreen.MatchingSpaceScreen-> NavEntry(key){
                        MatchingSpaceScreen(matchingViewModel)
                    }

                    is FullScreen.MatchingArtworkScreen-> NavEntry(key){
                        MatchingArtworkScreen(matchingViewModel)
                    }

                    is FullScreen.MatchingFeeScreen-> NavEntry(key){
                        MatchingFeeScreen(matchingViewModel)
                    }

                    is FullScreen.MatchingNoteScreen-> NavEntry(key){
                        MatchingNoteScreen(matchingViewModel)
                    }

                    is FullScreen.MatchingDateScreen-> NavEntry(key){
                        MatchingDateScreen(key.myId,key.myRole,key.partnerId, matchingViewModel)
                    }

                    else -> throw IllegalArgumentException("Unknown route: $key")
                }
            }
        )
    }
}

private fun mapTab(
    currentRoute: NavKey?
): MainTab = when (currentRoute) {
    is BottomNavScreen.Home -> MainTab.HOME
    is BottomNavScreen.Cafe -> MainTab.CAFE
    is BottomNavScreen.Artwork -> MainTab.ARTWORK
    is BottomNavScreen.Chat -> MainTab.CHAT
    is BottomNavScreen.Profile -> MainTab.PROFILE
    else -> MainTab.HOME
}