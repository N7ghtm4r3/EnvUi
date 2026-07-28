package com.tecknobit.envui.theme

import com.intellij.ui.JBColor
import com.intellij.ui.RoundedLineBorder
import com.intellij.util.ui.JBUI
import java.awt.Color
import javax.swing.border.Border

const val DEFAULT_RADIUS = 16

fun roundedBorder(
    color: Color = JBColor.border(),
    radius: Int = DEFAULT_RADIUS,
    innerPadding: Int = 12,
): Border {
    return JBUI.Borders.compound(
        RoundedLineBorder(
            color = color,
            arcDiameter = radius
        ),
        JBUI.Borders.empty(innerPadding)
    )!!
}