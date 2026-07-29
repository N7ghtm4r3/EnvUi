@file:OptIn(ExperimentalResourceApi::class)

package com.tecknobit.envui.ui.envuiwindow.presenter

import androidx.compose.runtime.CompositionLocalProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.tecknobit.envui.ui.envuiwindow.components.EnvUiWindowContent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.JvmResourceReader
import org.jetbrains.compose.resources.LocalResourceReader
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
        toolWindow.addComposeTab {
            val jvmResourceReaderClassLoader = JvmResourceReader(EnvUiWindow::class.java.classLoader)

            CompositionLocalProvider(LocalResourceReader provides jvmResourceReaderClassLoader) {
                EnvUiWindowContent(
                    project = project
                )
            }
        }
    }

}