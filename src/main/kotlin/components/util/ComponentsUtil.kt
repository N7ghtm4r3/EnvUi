package com.tecknobit.envui.components.util

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