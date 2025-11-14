package com.ssafy.yeobaeg.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.ssafy.yeobaeg.navigation.navigator.InternalNavigator
import com.ssafy.yeobaeg.navigation.navigator.InternalRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

sealed interface RouteSideEffect {
    data class Navigate(
        val route: NavKey
    ) : RouteSideEffect

    data class NavigateBack(
        val route: NavKey?,
        val popUpTo: Boolean = false
    ) : RouteSideEffect

    data class NavigateAndClearBackStack(
        val route: NavKey
    ) : RouteSideEffect
}

@HiltViewModel
internal class NavigatorViewModel @Inject constructor(
    navigator: InternalNavigator,
) : ViewModel() {
    val sideEffect by lazy(LazyThreadSafetyMode.NONE) {
        navigator.channel.receiveAsFlow()
            .map { navigator ->
                Log.d("LaunchedNavigator", "NavigatorViewModel received route: $navigator")
                when (navigator) {
                    is InternalRoute.Navigate ->
                        RouteSideEffect.Navigate(
                            navigator.route,
                        )

                    is InternalRoute.NavigateBack -> RouteSideEffect.NavigateBack(navigator.route, navigator.poopUpTo)

                    is InternalRoute.NavigateAndClearBackStack ->
                        RouteSideEffect.NavigateAndClearBackStack(navigator.route)
                }
            }
    }
}
