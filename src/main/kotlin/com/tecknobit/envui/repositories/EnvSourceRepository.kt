package com.tecknobit.envui.repositories

import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.writeAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFileType
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.util.toEnvSource

class EnvSourceRepository(
    private val project: Project,
) {

    suspend fun retrieveEnvs(
        filters: String,
    ): List<EnvSource> {
        val virtualFilesEnvTemplates = retrieveEnvTemplates()

        return readAction {
            val globalSearchScope = GlobalSearchScope.projectScope(project)
            val virtualFilesEnv = FileTypeIndex.getFiles(
                dEnvFileType,
                globalSearchScope
            )

            virtualFilesEnv.toEnvSourcesWithFilters(
                project = project,
                filters = filters,
                templates = virtualFilesEnvTemplates
            )
        }
    }

    suspend fun retrieveEnvTemplates(): Collection<VirtualFile> {
        return readAction {
            val globalSearchScope = GlobalSearchScope.projectScope(project)
            FileTypeIndex.getFiles(
                dEnvTemplateFileType,
                globalSearchScope
            )
        }
    }

    private fun Collection<VirtualFile?>.toEnvSourcesWithFilters(
        project: Project,
        filters: String,
        templates: Collection<VirtualFile?>,
    ): List<EnvSource> {
        val sources = toEnvSources(
            project = project,
            templates = templates
        )

        return sources.filter { source ->
            val containerFolder = source.containerFolder!!
            val module = source.module
            val containerFolderMatches = containerFolder.name.contains(filters)
            val moduleMatches = module?.name?.contains(filters) == true

            containerFolderMatches || moduleMatches
        }.sortedByDescending { source ->
            source.source.timeStamp
        }
    }

    private fun Collection<VirtualFile?>.toEnvSources(
        project: Project,
        templates: Collection<VirtualFile?>,
    ): List<EnvSource> {
        return this.map { file ->
            val template = templates.firstOrNull { template ->
                template?.parent?.path == file!!.parent.path
            }

            file!!.toEnvSource(
                project = project,
                template = template
            )
        }
    }

    suspend fun createNewEnvSource(
        project: Project,
        containerDirectory: PsiDirectory
    ): EnvSource {
        val dEnvExtension = ".${dEnvFileType.defaultExtension}"

        val source = writeAction {
            containerDirectory.createFile("$dEnvExtension.${dEnvTemplateFileType.defaultExtension}")
            containerDirectory.createFile(dEnvExtension)
        }

        return source.virtualFile.toEnvSource(
            project = project
        )
    }

}
