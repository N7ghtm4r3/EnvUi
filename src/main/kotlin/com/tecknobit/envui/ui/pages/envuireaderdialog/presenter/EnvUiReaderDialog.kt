package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuireaderdialog.presenter

import com.intellij.openapi.ui.DialogWrapper
import java.awt.Label
import javax.swing.JComponent
import javax.swing.JPanel

class EnvUiReaderDialog : DialogWrapper(
    true
) {

    init {

        super.init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()

        panel.add(Label("gweg"))

        return panel
    }

}