package com.tecknobit.envui

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.tecknobit.envui.components.envsource.EnvSourcesList
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.envui_window_title
import com.tecknobit.envui.repositories.EnvSourceRepository
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.bridge.addComposeTab
import org.jetbrains.jewel.ui.component.Text

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
            Text(
                text = stringResource(Res.string.envui_window_title)
            )
        }

        val contentFactory = ContentFactory.getInstance()

        DumbService.getInstance(project).runWhenSmart {
            val windowContent = windowContent(
                project = project
            )

            val content = contentFactory.createContent(windowContent, "", true)
            toolWindow.contentManager.addContent(content)
        }
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
