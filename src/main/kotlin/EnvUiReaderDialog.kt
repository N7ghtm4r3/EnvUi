package com.tecknobit.envui

import com.intellij.openapi.ui.DialogWrapper
import java.awt.Label
import javax.swing.JComponent
import javax.swing.JPanel

class EnvUiReaderDialog : DialogWrapper(
    true
) {

    init {
        title = I18nMessageBundle.message(
            key = "envui.dialog.title"
        )

        super.init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()

        panel.add(Label("gweg"))

        return panel
    }

}