package com.tecknobit.envui.ide.providers.envsource

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.containsEnvTemplate
import com.tecknobit.envui.utils.isNotEnvFile

/**
 * The `EnvSourceEditorNotificationProvider` class is useful to manage editor notifications for environment sources
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see EnvEditorNotificationProviderFactory
 *
 * @since 1.0.1
 */
class EnvSourceEditorNotificationProvider : EnvEditorNotificationProviderFactory() {

    /**
     * Method used to check whether this virtual file is not an environment source
     *
     * @receiver The virtual file to check
     *
     * @return whether the file is not an environment source as [Boolean]
     */
    override fun VirtualFile.isTargetFile(): Boolean {
        return isNotEnvFile()
    }

    /**
     * Method used to check whether the source container includes an environment template
     *
     * @param containerDirectory The directory containing the environment source
     *
     * @return whether the information panel can be displayed as [Boolean]
     */
    override fun canShowInfoPanel(
        containerDirectory: VirtualFile
    ): Boolean {
        return containerDirectory.containsEnvTemplate()
    }

    /**
     * Method used to get the warning message for a missing environment template
     *
     * @return the localized warning panel message as [String]
     */
    override fun warningPanelMessage(): @NlsSafe String {
        return I18nMessageBundle.message(
            key = "create.missing.env.template.warning"
        )
    }

    /**
     * Method used to create the environment template related to a source
     *
     * @param project The project containing the environment source
     * @param repository The repository used to create the environment template
     * @param file The environment source whose template must be created
     *
     * @return the environment source resolved after creating its template as [EnvSource]
     */
    override suspend fun warningPanelAction(
        project: Project,
        repository: EnvSourceRepository,
        file: VirtualFile,
    ): EnvSource {
        return repository.createNewEnvTemplateFromSource(
            project = project,
            envSource = file
        )
    }

}