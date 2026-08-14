package com.tecknobit.envui.ide.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog
import com.tecknobit.envui.util.toEnvSource

class OpenEnvSourceDialogAction : AnAction() {

    override fun actionPerformed(
        event: AnActionEvent
    ) {
        val editorFile = event.resolveVirtualFile() ?: return
        val project = event.project ?: return
        val envSource = runReadAction {
            editorFile.toEnvSource(
                project = project
            )
        }

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

    private fun AnActionEvent.resolveVirtualFile(): VirtualFile? {
        val editorFile = getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
        val fileType = editorFile.fileType
        if(fileType != dEnvFileType)
            return null

        return editorFile
    }

}