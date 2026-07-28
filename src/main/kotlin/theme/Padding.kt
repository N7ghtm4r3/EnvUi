package com.tecknobit.envui.theme

import com.intellij.util.ui.JBUI
import javax.swing.border.Border

fun bordersWithInnerPadding(
    padding: Int = 16,
): Border {
    return JBUI.Borders.compound(
        null,
        JBUI.Borders.empty(padding)
    )!!
}