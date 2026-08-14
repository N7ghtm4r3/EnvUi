package com.tecknobit.envui.ide.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog

class OpenEnvSourceDialogAction : AnAction() {

    override fun actionPerformed(
        event: AnActionEvent
    ) {
        val envSource = event.resolveEnvSource() ?: return
        val dialog = EnvSourceReaderDialog(
            envSource = envSource
        )

        dialog.show()
    }

    override fun update(event: AnActionEvent) {
        val editorFile = event.resolveVirtualFile()

        event.presentation.isEnabled = editorFile != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}