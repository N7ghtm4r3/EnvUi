package com.tecknobit.envui.com.tecknobit.envui.repositories

import com.intellij.openapi.application.readAction
import com.intellij.openapi.module.ModuleUtilCore
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

    suspend fun retrieveEnvs(
        filters: String,
    ): List<EnvSource> {
        return readAction {
            val virtualFiles = FilenameIndex.getVirtualFilesByName(
                ENV_EXT,
                GlobalSearchScope.projectScope(project)
            )

            val sources = virtualFiles.map { file ->
                EnvSource(
                    project = project,
                    module = ModuleUtilCore.findModuleForFile(file, project),
                    source = file
                )
            }

            sources.filter { source ->
                val containerFolder = source.containerFolder
                val module = source.module
                val containerFolderMatches = containerFolder.name.contains(filters)
                val moduleMatches = module?.name?.contains(filters) == true

                containerFolderMatches || moduleMatches
            }.sortedByDescending { source -> source.source.timeStamp }
        }
    }

}