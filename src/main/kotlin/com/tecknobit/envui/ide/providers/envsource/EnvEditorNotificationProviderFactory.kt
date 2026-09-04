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

/**
 * The `EnvEditorNotificationProviderFactory` class is useful to create editor notifications for environment files
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see EditorNotificationProvider
 *
 * @since 1.0.1
 */
abstract class EnvEditorNotificationProviderFactory : EditorNotificationProvider {

    /**
     * Method used to create the notification component for a supported environment file
     *
     * @param project The project containing the file
     * @param file The file whose notification data must be collected
     *
     * @return the notification component factory, if the file is supported, as [Function]
     */
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

    /**
     * Method used to check whether this virtual file must be excluded from the provider
     *
     * @receiver The virtual file to check
     *
     * @return whether the file must be excluded as [Boolean]
     */
    protected abstract fun VirtualFile.isTargetFile(): Boolean

    /**
     * Method used to check whether the information panel can be displayed for a file container
     *
     * @param containerDirectory The directory containing the environment file
     *
     * @return whether the information panel can be displayed as [Boolean]
     */
    protected abstract fun canShowInfoPanel(
        containerDirectory: VirtualFile
    ): Boolean

    /**
     * Method used to create the panel for opening an environment source in the reader dialog
     *
     * @param envSource The environment source opened by the panel action
     *
     * @return the information panel as [EditorNotificationPanel]
     */
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

    /**
     * Method used to create the panel for creating a missing related environment file
     *
     * @param project The project containing the environment file
     * @param file The environment file whose related file is missing
     *
     * @return the warning panel as [EditorNotificationPanel]
     */
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

    /**
     * Method used to get the localized warning panel message
     *
     * @return the localized warning panel message as [String]
     */
    protected abstract fun warningPanelMessage(): @NlsSafe String

    /**
     * Method used to create the related environment file missing from the file container
     *
     * @param project The project containing the environment file
     * @param repository The repository used to create the missing file
     * @param file The environment file whose related file must be created
     *
     * @return the environment source resolved after creating the missing file as [EnvSource]
     */
    protected abstract suspend fun warningPanelAction(
        project: Project,
        repository: EnvSourceRepository,
        file: VirtualFile,
    ): EnvSource

}