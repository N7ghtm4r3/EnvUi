package com.tecknobit.envui.ide.providers.envsource

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationPanel.Status.Info
import com.intellij.ui.EditorNotificationPanel.Status.Warning
import com.intellij.ui.EditorNotificationProvider
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.presenter.EnvSourceReaderDialog
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.toEnvSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.function.Function
import javax.swing.JComponent

abstract class EnvEditorNotificationProviderFactory : EditorNotificationProvider {

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile,
    ): Function<in FileEditor, out JComponent?>? {
        if (file.isTargetFile())
            return null

        return Function {
            val canShowInfoPanel = canShowInfoPanel(
                containerDirectory = file.parent
            )

            if (canShowInfoPanel) {
                val envSource = file.toEnvSource(
                    project = project
                )

                infoPanel(
                    envSource = envSource
                )
            } else {
                warningPanel(
                    project = project,
                    file = file
                )
            }
        }
    }

    protected abstract fun VirtualFile.isTargetFile(): Boolean

    protected abstract fun canShowInfoPanel(
        containerDirectory: VirtualFile
    ): Boolean

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
        file: VirtualFile,
    ): EditorNotificationPanel {
        val envSourceRepository = EnvSourceRepository(project)
        val scope = CoroutineScope(Dispatchers.Main)

        return EditorNotificationPanel(Warning).apply {
            text = warningPanelMessage()

            createActionLabel(
                I18nMessageBundle.message(
                    key = "create"
                )
            ) {
                scope.launch {
                    warningPanelAction(
                        project = project,
                        repository = envSourceRepository,
                        file = file,
                    )
                }
            }
        }
    }

    protected abstract fun warningPanelMessage(): @NlsSafe String

    protected abstract suspend fun warningPanelAction(
        project: Project,
        repository: EnvSourceRepository,
        file: VirtualFile,
    ): EnvSource

}