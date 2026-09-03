package com.tecknobit.envui.ide.providers.envsource

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.containsEnvSource
import com.tecknobit.envui.utils.isNotEnvTemplateFile

class EnvSourceTemplateEditorNotificationProvider : EnvEditorNotificationProviderFactory() {

    override fun VirtualFile.isTargetFile(): Boolean {
        return isNotEnvTemplateFile()
    }

    override fun canShowInfoPanel(
        containerDirectory: VirtualFile
    ): Boolean {
        return containerDirectory.containsEnvSource()
    }

    override fun warningPanelMessage(): @NlsSafe String {
        return I18nMessageBundle.message(
            key = "create.missing.env.source.warning"
        )
    }

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