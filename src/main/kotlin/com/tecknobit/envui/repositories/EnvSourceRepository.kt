package com.tecknobit.envui.repositories

import com.intellij.openapi.application.readAction
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.tecknobit.envui.ide.envfile.dEnvFileType
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

class EnvSourceRepository(
    private val project: Project,
) {

    suspend fun retrieveEnvs(
        filters: String,
    ): List<EnvSource> {
        return readAction {
            val virtualFiles = FileTypeIndex.getFiles(
                dEnvFileType,
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
        val psiManager = PsiManager.getInstance(project)

        return this.map { file ->
            EnvSource(
                project = project,
                source = file!!,
                module = ModuleUtilCore.findModuleForFile(file, project),
                psiSource = psiManager.findFile(file)!!
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
