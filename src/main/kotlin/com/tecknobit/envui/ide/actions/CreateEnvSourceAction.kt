package com.tecknobit.envui.ide.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.writeAction
import com.intellij.psi.PsiDirectory
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFileType
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog
import com.tecknobit.envui.util.toEnvSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateEnvSourceAction : AnAction() {

    override fun actionPerformed(
        event: AnActionEvent
    ) {
        val selectedDirectory = event.resolveSelectedDirectory() ?: return
        val scope = CoroutineScope(Dispatchers.IO)
        val dEnvExtension = ".${dEnvFileType.defaultExtension}"

        scope.launch {
            val source = writeAction {
                selectedDirectory.createFile("$dEnvExtension.${dEnvTemplateFileType.defaultExtension}")
                selectedDirectory.createFile(dEnvExtension)
            }

            val envSource = source.virtualFile.toEnvSource(
                project = event.project!!
            )

            invokeLater {
                val envSourceReaderDialog = EnvSourceReaderDialog(
                    envSource = envSource
                )

                envSourceReaderDialog.show()
            }
        }
    }

    override fun update(
        event: AnActionEvent
    ) {
        val selectedDirectory = event.resolveSelectedDirectory()
        val presentation = event.presentation
        //TODO: TO CHANGE WITH LOGO
        presentation.icon = AllIcons.Actions.AddFile

        if(selectedDirectory == null) {
            presentation.isEnabled = false
            return
        }

        val envSource = selectedDirectory.findFile(".${dEnvFileType.defaultExtension}")
        if(envSource != null) {
            presentation.isEnabled = false
            return
        }

        presentation.isEnabled = true
    }

    private fun AnActionEvent.resolveSelectedDirectory(): PsiDirectory? {
        val selectedFile = getData(LangDataKeys.IDE_VIEW)?.orChooseDirectory
        if(selectedFile == null || !selectedFile.isDirectory)
            return null

        return selectedFile
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}