package com.tecknobit.envui.ide.providers.envsource

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationPanel.Status.Info
import com.intellij.ui.EditorNotificationPanel.Status.Warning
import com.intellij.ui.EditorNotificationProvider
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.containsEnvSource
import com.tecknobit.envui.utils.isEnvFile
import com.tecknobit.envui.utils.isNotEnvTemplateFile
import com.tecknobit.envui.utils.toEnvSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.function.Function
import javax.swing.JComponent

/**
 * The `EnvSourceEditorNotificationProvider` class is useful to offer the dialog editor for environment source files
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class EnvSourceTemplateEditorNotificationProvider : EditorNotificationProvider {

    /**
     * Method used to create notification content for environment source editors
     *
     * @param project The project containing the file
     * @param file The virtual file displayed by the editor
     *
     * @return the notification component provider, if the file is an environment source, as [Function]
     */
    override fun collectNotificationData(
        project: Project,
        file: VirtualFile,
    ): Function<in FileEditor, out JComponent?>? {
        if (file.isNotEnvTemplateFile())
            return null

        val envSourceIsExists = file.parent.containsEnvSource()
        val showInfoPanel = file.isEnvFile() || envSourceIsExists
        return Function {
            if (showInfoPanel) {
                val envSource = file.toEnvSource(
                    project = project
                )

                infoPanel(
                    envSource = envSource
                )
            } else {
                warningPanel(
                    project = project,
                    envTemplateFile = file
                )
            }
        }
    }

    private fun infoPanel(
        envSource: EnvSource,
    ): EditorNotificationPanel {
        return EditorNotificationPanel(Info).apply {
            text = I18nMessageBundle.message(
                key = "edit.env.in.dialog"
            )

            createActionLabel(
                I18nMessageBundle.message(
                    key = "open"
                )
            ) {
                val dialog = EnvSourceReaderDialog(
                    envSource = envSource
                )

                dialog.show()
            }
        }
    }


    private fun warningPanel(
        project: Project,
        envTemplateFile: VirtualFile,
    ): EditorNotificationPanel {
        val envSourceRepository = EnvSourceRepository(project)
        val scope = CoroutineScope(Dispatchers.Main)

        return EditorNotificationPanel(Warning).apply {
            text = I18nMessageBundle.message(
                key = "create.missing.env.source.warning"
            )

            createActionLabel(
                I18nMessageBundle.message(
                    key = "create"
                )
            ) {
                scope.launch {
                    envSourceRepository.createNewEnvSourceFromTemplate(
                        envTemplate = envTemplateFile
                    )
                }
            }
        }
    }

}