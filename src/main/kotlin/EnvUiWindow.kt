package com.tecknobit.envui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.tecknobit.envui.components.EnvSourcesList
import com.tecknobit.envui.repositories.EnvSourceRepository

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
        val contentFactory = ContentFactory.getInstance()

        val windowContent = windowContent(
            project = project
        )

        val content = contentFactory.createContent(windowContent, "", true)
        toolWindow.contentManager.addContent(content)
    }

    private fun windowContent(
        project: Project,
    ): DialogPanel {
        val envSourceRepository = EnvSourceRepository(
            project = project
        )

        val list = EnvSourcesList(
            sources = envSourceRepository.retrieveEnvs()
        )

        return panel {
            row {
                scrollCell(list)
                    .resizableColumn()
                    .align(Align.FILL)
            }.resizableRow()
        }
    }

}
