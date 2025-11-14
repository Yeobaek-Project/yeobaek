package com.ssafy.yeobaeg.navigation

import androidx.navigation3.runtime.NavKey
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey

sealed interface FullScreen : Screen {
    @Serializable
    data object Splash : FullScreen

    @Serializable
    data object Onboarding : FullScreen

    @Serializable
    data object Login : FullScreen

    @Serializable
    data object SignUp : FullScreen

    @Serializable
    data class ProfileEdit(val userInfo: UserInfo, val introduction: String?) : FullScreen

    @Serializable
    data object SpaceScan : FullScreen

    @Serializable
    data object RegisterCafe : FullScreen

    @Serializable
    data object RegisterArtwork : FullScreen

    @Serializable
    data class ArtworkDetail(val artworkId: Long) : FullScreen

    @Serializable
    data class CafeDetail(val cafeId: Long) : FullScreen

    @Serializable
    data object BookmarkList : FullScreen

    @Serializable
    data object ReviewManagementScreen : FullScreen

    @Serializable
    data class ChattingScreen(val chatRoomId:Long): FullScreen

    @Serializable
    data class EditDisplaySlotScreen(
        val cafeId: Long,
        val slotId: Long,
        val commissionRate: Int,
        val requirements: String
    ) : FullScreen

    @Serializable
    data class WallEdit(val displayWalls: List<DisplaySlot>) : FullScreen

    @Serializable
    data class ArtistProfile(val artistId: Long) : FullScreen

    @Serializable
    data class ExhibitionCalendar(
        val slotId: Long,
        val slotName: String,
        val width: Float,
        val height: Float,
        val commission: Float,
        val requirements: String
    ) : FullScreen

    @Serializable
    data object ArtistHomeMatching : FullScreen

    @Serializable
    data class CafeOwnerMatching(val slots: List<DisplaySlot>) : FullScreen
}

sealed interface BottomNavScreen : Screen {
    // 홈 화면들
    @Serializable
    data object Home : BottomNavScreen

    @Serializable
    data object Cafe : BottomNavScreen

    @Serializable
    data object Artwork : BottomNavScreen

    @Serializable
    data object Chat : BottomNavScreen

    @Serializable
    data class Profile(val userId: Long?) : BottomNavScreen
}