package com.tecknobit.envui.components.util

import org.jetbrains.annotations.Range
import java.awt.Color

fun Color.withAlpha(
    alpha: @Range(from = 0, to = 1) Float = 1f,
): Color {
    val alphaValue = (255 * alpha).toInt()

    return Color(red, blue, green, alphaValue)
}