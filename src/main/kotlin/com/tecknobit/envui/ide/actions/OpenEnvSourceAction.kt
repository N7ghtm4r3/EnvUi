package com.tecknobit.envui.ide.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog

class OpenEnvSourceAction : AnAction() {

    override fun actionPerformed(
        event: AnActionEvent
    ) {
        val envSource = event.resolveEnvSource() ?: return
        val dialog = EnvSourceReaderDialog(
            envSource = envSource
        )

        dialog.show()
    }

    override fun update(
        event: AnActionEvent
    ) {
        val editorFile = event.resolveVirtualFile()

        val presentation = event.presentation
        presentation.isEnabled = editorFile != null
        presentation.icon = AllIcons.Actions.Preview
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}