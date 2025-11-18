package com.ssafy.yeobaeg.presentation.main

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.WindowInsetsRulers
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

fun Modifier.alignToSafeDrawing(): Modifier {
    return layout { measurable, constraints ->
        if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
            val placeable = measurable.measure(constraints)
            val width = placeable.width
            val height = placeable.height
            layout(width, height) {
                val bottom = WindowInsetsRulers.SafeDrawing.current.bottom
                    .current(0f).roundToInt() - height
                val right = WindowInsetsRulers.SafeDrawing.current.right
                    .current(0f).roundToInt()
                val left = WindowInsetsRulers.SafeDrawing.current.left
                    .current(0f).roundToInt()
                measurable.measure(Constraints.fixed(right - left, height))
                    .place(left, bottom)
            }
        } else {
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
            }
        }
    }
}