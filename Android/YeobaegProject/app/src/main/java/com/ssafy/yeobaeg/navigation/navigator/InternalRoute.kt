package com.ssafy.yeobaeg.navigation.navigator

import androidx.navigation3.runtime.NavKey

sealed interface InternalRoute {
    data class Navigate(
        val route: NavKey,
        val popUpTo: Boolean
    ) : InternalRoute

    data object NavigateBack : InternalRoute

    data class NavigateAndClearBackStack(
        val route: NavKey
    ) : InternalRoute
}