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

class EnvSourceEditorNotificationProvider : EditorNotificationProvider {

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