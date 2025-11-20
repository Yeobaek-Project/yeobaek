package com.ssafy.yeobaeg.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ProfileViewModel @Inject constructor(
    private val navigator: Navigator,
    private val authSessionManager: AuthSessionManager
) : ViewModel() {
    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

    fun navigateToBookmark() = viewModelScope.launch {
        navigator.navigate(FullScreen.BookmarkList)
    }

    fun navigateToEditProfile(userInfo: UserInfo, introduction:String?) = viewModelScope.launch {
        navigator.navigate(FullScreen.ProfileEdit(userInfo,introduction))
    }
    fun navigateToReviewManagement() = viewModelScope.launch {
        navigator.navigate(FullScreen.ReviewManagementScreen)
    }

    fun logout() = viewModelScope.launch {
        authSessionManager.logout()
    }
}