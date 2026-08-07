package com.tecknobit.envui.ui.components

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.NlsContexts.DialogTitle
import org.jetbrains.jewel.bridge.compose
import java.awt.Dimension
import javax.swing.Action
import javax.swing.JComponent

abstract class EnvUiDialog<V: ViewModel>(
    protected val viewModel: V,
    title: @DialogTitle String,
    canBeParent: Boolean = true
): DialogWrapper(
    canBeParent
) {

    init {
        super.title = title

        super.init()
    }

    override fun createCenterPanel(): JComponent? {
        return compose(
            focusOnClickInside = true,
            config = {
                preferredSize = Dimension(600, 500)
            },
            content = {
                DialogContent()
            }
        )
    }

    override fun createActions(): Array<out Action?> {
        return emptyArray()
    }

    @Composable
    protected abstract fun DialogContent()

}