package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envsourcereaderdialog.presenter

import com.intellij.openapi.ui.DialogWrapper
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import org.jetbrains.jewel.bridge.compose
import org.jetbrains.jewel.ui.component.Text
import javax.swing.JComponent

class EnvSourceReaderDialog(
    envSource: EnvSource,
) : DialogWrapper(
    true
) {

    init {
        title = envSource.name + " TODO"

        super.init()
    }

    override fun createCenterPanel(): JComponent {
        return compose(
            focusOnClickInside = true
        ) {
            Text(
                text = "gag"
            )
        }
    }

}