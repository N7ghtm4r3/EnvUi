package com.tecknobit.envui.ui.components

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.NlsContexts.DialogTitle
import com.tecknobit.envui.ui.helpers.StringResourcesProvider
import org.jetbrains.jewel.bridge.compose
import java.awt.Dimension
import javax.swing.Action
import javax.swing.JComponent

/**
 * The `EnvUiDialog` class is useful to host Compose dialog content supported by a viewmodel
 *
 * @param V The type of viewmodel supporting the dialog
 * @property viewModel The viewmodel supporting the dialog content
 * @param title The title displayed by the dialog
 * @param canBeParent Whether the dialog can act as a parent window
 *
 * @author N7ghtm4r3 - Tecknobit
 */
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

    /**
     * Method used to create the Compose panel displayed at the center of the dialog
     *
     * @return the configured center panel as [JComponent]
     */
    override fun createCenterPanel(): JComponent? {
        return compose(
            focusOnClickInside = true,
            config = {
                preferredSize = Dimension(600, 500)
            },
            content = {
                StringResourcesProvider(
                    context = this::class
                ) {
                    DialogContent()
                }
            }
        )
    }

    /**
     * Method used to create the actions exposed by the dialog
     *
     * @return the dialog actions as [Array] of [Action]
     */
    override fun createActions(): Array<out Action?> {
        return arrayOf(super.okAction)
    }

    /**
     * The custom content displayed in the dialog
     */
    @Composable
    protected abstract fun DialogContent()

}