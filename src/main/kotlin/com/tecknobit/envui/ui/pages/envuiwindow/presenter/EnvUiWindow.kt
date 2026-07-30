package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.presenter

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.content.EnvUiWindowContent
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.presentation.EnvUiWindowViewModel
import org.jetbrains.jewel.bridge.addComposeTab

class EnvUiWindow : ToolWindowFactory {

    override fun shouldBeAvailable(
        project: Project,
    ): Boolean {
        return true
    }

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