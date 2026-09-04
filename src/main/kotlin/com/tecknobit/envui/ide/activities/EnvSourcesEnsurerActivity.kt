package com.tecknobit.envui.ide.activities

import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.repositories.EnvSourceRepository
import com.tecknobit.envui.utils.containsEnvSource

/**
 * The `EnvSourcesEnsurerActivity` class is useful to create missing environment sources for project templates
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see ProjectActivity
 *
 * @since 1.0.1
 */
class EnvSourcesEnsurerActivity : ProjectActivity {

    /**
     * Method used to ensure each environment template has a related environment source
     *
     * @param project The initialized project
     */
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

    /**
     * Method used to create the environment source related to a template when missing
     *
     * @param repository The repository used to create the environment source
     * @param envTemplate The environment template whose source must be ensured
     */
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