package com.tecknobit.envui.ide.providers.envsource

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.containsEnvSource
import com.tecknobit.envui.utils.isNotEnvTemplateFile

/**
 * The `EnvSourceTemplateEditorNotificationProvider` class is useful to manage editor notifications for environment templates
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see EnvEditorNotificationProviderFactory
 *
 * @since 1.0.1
 */
class EnvSourceTemplateEditorNotificationProvider : EnvEditorNotificationProviderFactory() {

    /**
     * Method used to check whether this virtual file is not an environment template
     *
     * @receiver The virtual file to check
     *
     * @return whether the file is not an environment template as [Boolean]
     */
    override fun VirtualFile.isTargetFile(): Boolean {
        return isNotEnvTemplateFile()
    }

    /**
     * Method used to check whether the template container includes an environment source
     *
     * @param containerDirectory The directory containing the environment template
     *
     * @return whether the information panel can be displayed as [Boolean]
     */
    override fun canShowInfoPanel(
        containerDirectory: VirtualFile
    ): Boolean {
        return containerDirectory.containsEnvSource()
    }

    /**
     * Method used to get the warning message for a missing environment source
     *
     * @return the localized warning panel message as [String]
     */
    override fun warningPanelMessage(): @NlsSafe String {
        return I18nMessageBundle.message(
            key = "create.missing.env.source.warning"
        )
    }

    /**
     * Method used to create the environment source related to a template
     *
     * @param project The project containing the environment template
     * @param repository The repository used to create the environment source
     * @param file The environment template whose source must be created
     *
     * @return the environment source created from the template as [EnvSource]
     */
    override suspend fun warningPanelAction(
        project: Project,
        repository: EnvSourceRepository,
        file: VirtualFile,
    ): EnvSource {
        return repository.createNewEnvSourceFromTemplate(
            envTemplate = file
        )
    }

}