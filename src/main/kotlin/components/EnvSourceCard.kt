package com.tecknobit.envui.components

import com.intellij.ui.components.JBLabel
import com.tecknobit.envui.data.EnvSource
import com.tecknobit.envui.theme.roundedBorder
import java.awt.BorderLayout
import javax.swing.JPanel

class EnvSourceCard(
    private val envSource: EnvSource,
) : JPanel() {

    init {
        layout = BorderLayout()
        border = roundedBorder()

        add(JBLabel(envSource.name))
    }

}