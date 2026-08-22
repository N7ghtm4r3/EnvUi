package com.tecknobit.envui.ide.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog

/**
 * The `OpenEnvSourceAction` class is useful to open the selected environment source in its editor dialog
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class OpenEnvSourceAction : AnAction() {

    /**
     * Method used to open the selected environment source
     *
     * @param event The action event containing the selected source
     */
    override fun actionPerformed(
        event: AnActionEvent
    ) {
        val envSource = event.resolveEnvSource() ?: return
        val dialog = EnvSourceReaderDialog(
            envSource = envSource
        )

        dialog.show()
    }

    /**
     * Method used to update the action availability and icon from the selected file
     *
     * @param event The action event to update
     */
    override fun update(
        event: AnActionEvent
    ) {
        val editorFile = event.resolveVirtualFile()

        val presentation = event.presentation
        presentation.isEnabled = editorFile != null
        presentation.icon = AllIcons.Actions.Preview
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