package com.tecknobit.envui.com.tecknobit.envui.repositories

import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

class EnvSourceRepository(
    private val project: Project,
) {

    private companion object {

        const val ENV_EXT = ".env"

    }

    suspend fun retrieveEnvs(): List<EnvSource> {
        return readAction {
            val virtualFiles = FilenameIndex.getVirtualFilesByName(
                ENV_EXT,
                GlobalSearchScope.projectScope(project)
            )

            virtualFiles.map {
                EnvSource(
                    project = project,
                    source = it
                )
            }
        }
    }

}