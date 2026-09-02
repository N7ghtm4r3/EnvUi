package com.tecknobit.envui.ide.activities

import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.utils.containsEnvSource

class EnvSourcesEnsurerActivity : ProjectActivity {

    override suspend fun execute(
        project: Project,
    ) {
        val repository = EnvSourceRepository(
            project = project
        )

        val templates = repository.retrieveEnvTemplates()
        templates.forEach { template ->
            ensureEnvSource(
                repository = repository,
                envTemplate = template
            )
        }
    }

    private suspend fun ensureEnvSource(
        repository: EnvSourceRepository,
        envTemplate: VirtualFile,
    ) {
        val containerDirectory = envTemplate.parent
        val sourceFileExists = readAction {
            containerDirectory.containsEnvSource()
        }
        if (sourceFileExists)
            return

        repository.createNewEnvSourceFromTemplate(
            envTemplate = envTemplate
        )
    }

}