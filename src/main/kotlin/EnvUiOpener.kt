package com.tecknobit.envui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class EnvUiOpener : ToolWindowFactory {

    override fun shouldBeAvailable(
        project: Project,
    ) = true

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow,
    ) {
        val envUiReaderDialog = EnvUiReaderDialog()

        envUiReaderDialog.show()
    }

}
