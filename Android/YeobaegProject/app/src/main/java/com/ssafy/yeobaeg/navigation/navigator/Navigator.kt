package com.ssafy.yeobaeg.navigation.navigator

import androidx.navigation3.runtime.NavKey

interface Navigator {

    suspend fun navigate(
        route: NavKey,
    )

    suspend fun navigateBack(route: NavKey? = null, popUpTo: Boolean = false)

    suspend fun navigateAndClearBackStack(route: NavKey)
}
