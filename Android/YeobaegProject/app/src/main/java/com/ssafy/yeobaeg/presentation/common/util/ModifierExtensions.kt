package com.ssafy.yeobaeg.presentation.common.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        enabled = enabled,
        role = role,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}


fun Modifier.advancedImePadding() = composed {
    var consumePadding by remember { mutableIntStateOf(0) }
    onGloballyPositioned { coordinates ->
        consumePadding = coordinates.findRootCoordinates().size.height -
                (coordinates.positionInWindow().y + coordinates.size.height).toInt().coerceAtLeast(0)
    }
        .consumeWindowInsets(
            PaddingValues(bottom = with(LocalDensity.current) { consumePadding.toDp() })
        )
        .imePadding()
}