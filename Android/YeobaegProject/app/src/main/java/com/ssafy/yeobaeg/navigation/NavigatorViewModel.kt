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
        val route: NavKey,
        val popUpTo: Boolean
    ) : RouteSideEffect

    data object NavigateBack : RouteSideEffect

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
                            navigator.popUpTo
                        )

                    is InternalRoute.NavigateBack -> RouteSideEffect.NavigateBack

                    is InternalRoute.NavigateAndClearBackStack ->
                        RouteSideEffect.NavigateAndClearBackStack(navigator.route)
                }
            }
    }
}
