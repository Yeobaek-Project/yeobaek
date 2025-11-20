package com.ssafy.yeobaeg.navigation.navigator

import androidx.navigation3.runtime.NavKey

sealed interface InternalRoute {
    data class Navigate(
        val route: NavKey
    ) : InternalRoute

    data class NavigateBack(
        val route: NavKey? = null,
        val poopUpTo: Boolean = false
    ) : InternalRoute

    data class NavigateAndClearBackStack(
        val route: NavKey
    ) : InternalRoute
}