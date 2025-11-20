package com.ssafy.yeobaeg.presentation.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavBackStack
import com.ssafy.yeobaeg.navigation.NavigatorViewModel
import com.ssafy.yeobaeg.navigation.RouteSideEffect
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LaunchedNavigator(navBackStack: NavBackStack) {
    InternalLaunchedNavigator(
        navBackStack = navBackStack,
    )
}

@Composable
private fun InternalLaunchedNavigator(
    navBackStack: NavBackStack,
    routerViewModel: NavigatorViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(routerViewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            routerViewModel.sideEffect.collectLatest { sideEffect ->
                Log.d("LaunchedNavigator", "Received side effect: $sideEffect")
                when (sideEffect) {
                    is RouteSideEffect.NavigateBack -> {
                        if (sideEffect.popUpTo) {
                            while (navBackStack.isNotEmpty() && navBackStack.last().javaClass != sideEffect.route?.javaClass) {
                                navBackStack.removeLastOrNull()
                            }
                        } else navBackStack.removeLastOrNull()
                    }

                    is RouteSideEffect.Navigate -> {
                        navBackStack.remove(sideEffect.route)
                        navBackStack.add(sideEffect.route)
                    }

                    is RouteSideEffect.NavigateAndClearBackStack -> {
                        navBackStack.clear()
                        navBackStack.add(sideEffect.route)
                    }
                }
            }
        }
    }
}
