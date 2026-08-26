package com.tecknobit.envui.ui.pages.envuiwindow.presenter

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.tecknobit.envui.ui.pages.envuiwindow.content.EnvUiWindowContent
import com.tecknobit.envui.ui.pages.envuiwindow.presentation.EnvUiWindowViewModel
import org.jetbrains.jewel.bridge.addComposeTab

/**
 * The [EnvUiWindow] displays the project environment sources in a JetBrains IDE tool window
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class EnvUiWindow : ToolWindowFactory {

    /**
     * Method used to check whether the environment source tool window is available for a project
     *
     * @param project The project requesting the tool window
     *
     * @return whether the tool window is available as [Boolean]
     */
    override fun shouldBeAvailable(
        project: Project,
    ): Boolean {
        return true
    }

    /**
     * Method used to create the Compose content of the environment source tool window
     *
     * @param project The project displayed by the tool window
     * @param toolWindow The tool window receiving the content
     */
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow,
    ) {
        val viewModel = EnvUiWindowViewModel(
            project = project
        )

        toolWindow.addComposeTab {
            EnvUiWindowContent(
                viewModel = viewModel,
                project = project
            )
        }
    }

}
