package com.tecknobit.envui.components

import org.jetbrains.annotations.Range
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JPanel

inline fun JPanel.addToPanel(
    component: () -> JComponent?,
) {
    val componentResult = component()

    componentResult?.let {
        add(it)
    }
}

fun Color.withAlpha(
    alpha: @Range(from = 0, to = 1) Float = 1f,
): Color {
    val alphaValue = (255 * alpha).toInt()

    return Color(red, blue, green, alphaValue)
}