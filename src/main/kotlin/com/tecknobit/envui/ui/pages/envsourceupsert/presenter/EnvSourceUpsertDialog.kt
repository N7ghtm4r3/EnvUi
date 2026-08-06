package com.tecknobit.envui.ui.pages.envsourceupsert.presenter

import com.intellij.openapi.ui.DialogWrapper
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import org.jetbrains.jewel.bridge.compose
import org.jetbrains.jewel.ui.component.Text
import javax.swing.JComponent

class EnvSourceUpsertDialog(
    envSource: EnvSource? = null,
) : DialogWrapper(
    true
) {

    private val viewModel = EnvSourceUpsertDialog(
        envSource = envSource
    )

    init {
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
