package com.tecknobit.envui.com.tecknobit.envui.repositories

import com.intellij.openapi.application.readAction
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.tecknobit.envui.com.tecknobit.envui.constants.ENV_EXTENSION
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

class EnvSourceRepository(
    private val project: Project,
) {

    suspend fun retrieveEnvs(
        filters: String,
    ): List<EnvSource> {
        return readAction {
            val virtualFiles = FilenameIndex.getVirtualFilesByName(
                ENV_EXTENSION,
                GlobalSearchScope.projectScope(project)
            )

            virtualFiles.toEnvSourcesWithFilters(
                project = project,
                filters = filters
            )
        }
    }

    private fun Collection<VirtualFile?>.toEnvSources(
        project: Project,
    ): List<EnvSource> {
        return this.map { file ->
            EnvSource(
                project = project,
                module = ModuleUtilCore.findModuleForFile(file!!, project),
                source = file
            )
        }
    }

    private fun Collection<VirtualFile?>.toEnvSourcesWithFilters(
        project: Project,
        filters: String,
    ): List<EnvSource> {
        val sources = toEnvSources(
            project = project
        )

        return sources.filter { source ->
            val containerFolder = source.containerFolder
            val module = source.module
            val containerFolderMatches = containerFolder.name.contains(filters)
            val moduleMatches = module?.name?.contains(filters) == true

            containerFolderMatches || moduleMatches
        }.sortedByDescending { source ->
            source.source.timeStamp
        }
    }

}