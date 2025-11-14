package com.ssafy.yeobaeg.navigation.navigator

import android.util.Log
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@ActivityRetainedScoped
class NavigatorImpl @Inject constructor() : Navigator, InternalNavigator {
    override val channel = Channel<InternalRoute>(Channel.BUFFERED)

    override suspend fun navigate(
        route: NavKey,
    ) {
        Log.d("LaunchedNavigator", "Navigating to route: $route")
        channel.send(
            InternalRoute.Navigate(
                route = route,
            ),
        )
    }

    override suspend fun navigateBack(
        route: NavKey?,
        popUpTo: Boolean
    ) {
        channel.send(InternalRoute.NavigateBack(
            route, popUpTo
        ))
    }

    override suspend fun navigateAndClearBackStack(route: NavKey) {
        channel.send(
            InternalRoute.NavigateAndClearBackStack(route = route)
        )
    }
}
