package com.tecknobit.envui.ide.providers

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog
import com.tecknobit.envui.utils.isNotEnvSourceFile
import com.tecknobit.envui.utils.toEnvSource
import java.util.function.Function
import javax.swing.JComponent

/**
 * The `EnvSourceEditorNotificationProvider` class is useful to offer the dialog editor for environment source files
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class EnvSourceEditorNotificationProvider : EditorNotificationProvider {

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
        if (file.isNotEnvSourceFile())
            return null

        return Function {
            warningPanel(
                project = project,
                file = file
            )
        }
    }

    /**
     * Method used to create the panel that opens an environment source in its dialog editor
     *
     * @param project The project containing the file
     * @param file The environment source or template file
     *
     * @return the configured notification panel as [EditorNotificationPanel]
     */
    private fun warningPanel(
        project: Project,
        file: VirtualFile,
    ): EditorNotificationPanel {
        val envSource = file.toEnvSource(
            project = project,
            resolveModule = false
        )

        return EditorNotificationPanel().apply {
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

}