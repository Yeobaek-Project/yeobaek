package com.ssafy.yeobaeg.navigation.navigator

import androidx.navigation3.runtime.NavKey

interface Navigator {

    suspend fun navigate(
        route: NavKey,
        popUpTo: Boolean = false
    )

    suspend fun navigateBack()

    suspend fun navigateAndClearBackStack(route: NavKey)
}
