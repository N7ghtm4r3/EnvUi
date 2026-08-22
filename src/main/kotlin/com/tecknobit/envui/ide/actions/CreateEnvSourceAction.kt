package com.tecknobit.envui.ide.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.invokeLater
import com.intellij.psi.PsiDirectory
import com.tecknobit.envui.ide.theme.EnvUiIcons
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The `CreateEnvSourceAction` class is useful to create an environment source in the selected directory
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class CreateEnvSourceAction : AnAction() {

    /**
     * Method used to create and open an environment source in the selected directory
     *
     * @param event The action event containing the selected directory and project
     */
    override fun actionPerformed(
        event: AnActionEvent
    ) {
        val selectedDirectory = event.resolveSelectedDirectory() ?: return
        val project = event.project!!
        val repository = EnvSourceRepository(
            project = project
        )

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val envSource = repository.createNewEnvSource(
                project = project,
                containerDirectory = selectedDirectory
            )

            invokeLater {
                val envSourceReaderDialog = EnvSourceReaderDialog(
                    envSource = envSource
                )

                envSourceReaderDialog.show()
            }
        }
    }

    /**
     * Method used to enable the action only for directories without an environment source
     *
     * @param event The action event to update
     */
    override fun update(
        event: AnActionEvent
    ) {
        val selectedDirectory = event.resolveSelectedDirectory()
        val presentation = event.presentation
        presentation.icon = EnvUiIcons.CreateEnvSource

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

    /**
     * Method used to resolve the directory selected by this action event
     *
     * @return the selected directory, if available, as [PsiDirectory]
     */
    private fun AnActionEvent.resolveSelectedDirectory(): PsiDirectory? {
        val selectedFile = getData(LangDataKeys.IDE_VIEW)?.orChooseDirectory
        if(selectedFile == null || !selectedFile.isDirectory)
            return null

        return selectedFile
    }

    /**
     * Method used to retrieve the thread used to update the action
     *
     * @return the background update thread as [ActionUpdateThread]
     */
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}
