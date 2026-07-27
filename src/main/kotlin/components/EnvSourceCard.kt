package com.tecknobit.envui.components

import com.intellij.ui.components.JBLabel
import com.tecknobit.envui.data.EnvSource
import javax.swing.JPanel

class EnvSourceCard(
    val envSource: EnvSource,
) : JPanel() {

    init {
        add(JBLabel(envSource.name))
    }

}