package com.tecknobit.envui.repositories

import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.writeAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.enums.EnvFieldType.ANY
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFileType
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.formatAsString
import com.tecknobit.envui.utils.toEnvSource
import com.tecknobit.envui.utils.writeContent

/**
 * The `EnvSourceRepository` class is useful to retrieve and create the environment sources of a project
 *
 * @property project The project where the environment sources are managed
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class EnvSourceRepository(
    private val project: Project,
) {

    /**
     * Method used to retrieve the environment sources matching the specified filter, ordered by latest update
     *
     * @param filters The filter to apply to container folder and module names
     *
     * @return the matching environment sources as [List] of [EnvSource]
     */
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

    /**
     * Method used to retrieve the environment template files of the project
     *
     * @return the environment template files as [Collection] of [VirtualFile]
     */
    suspend fun retrieveEnvTemplates(): Collection<VirtualFile> {
        return readAction {
            val globalSearchScope = GlobalSearchScope.projectScope(project)
            FileTypeIndex.getFiles(
                dEnvTemplateFileType,
                globalSearchScope
            )
        }
    }

    /**
     * Method used to convert the virtual files into filtered and ordered environment sources
     *
     * @receiver The virtual files to convert
     * @param project The project containing the virtual files
     * @param filters The filter to apply to container folder and module names
     * @param templates The environment template files used to resolve the sources
     *
     * @return the matching environment sources as [List] of [EnvSource]
     */
    private fun Collection<VirtualFile?>.toEnvSourcesWithFilters(
        project: Project = this@EnvSourceRepository.project,
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

    /**
     * Method used to convert the virtual files into environment sources with their matching templates
     *
     * @receiver The virtual files to convert
     * @param project The project containing the virtual files
     * @param templates The environment template files used to resolve the sources
     *
     * @return the environment sources as [List] of [EnvSource]
     */
    private fun Collection<VirtualFile?>.toEnvSources(
        project: Project = this@EnvSourceRepository.project,
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

    /**
     * Method used to create a new environment source and its template in the specified directory
     *
     * @param project The project where the environment source is created
     * @param containerDirectory The directory where the environment source and its template are created
     *
     * @return the created environment source as [EnvSource]
     */
    suspend fun createNewEnvSource(
        project: Project = this.project,
        containerDirectory: PsiDirectory,
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

    suspend fun createNewEnvTemplateFromSource(
        project: Project = this.project,
        envSource: VirtualFile,
    ): EnvSource {
        return createMissingSource(
            project = project,
            fileName = ".${dEnvFileType.defaultExtension}.${dEnvTemplateFileType.defaultExtension}",
            file = envSource,
            syncSources = { existing, newSource ->
                syncTemplateFromSource(
                    source = existing,
                    template = newSource
                )
            }
        )
    }

    private suspend fun syncTemplateFromSource(
        source: PsiFile,
        template: PsiFile,
    ) {
        val sourcePsiFile = (source as dEnvFile)
        val envTypes = mapEnvTypes(
            source = sourcePsiFile
        )

        println(envTypes)

        writeAction {
            val templateKeys = sourcePsiFile.keys()
            val formattedKeys = templateKeys.formatAsString()

            (template as dEnvTemplateFile).writeContent(
                content = formattedKeys
            )
        }
    }

    private fun mapEnvTypes(
        source: dEnvFile,
    ): Map<String, EnvFieldType> {
        val entryTypes = mutableMapOf<String, EnvFieldType>()
        val propertyEntries = source.properties()

        propertyEntries.forEach { propertyEntry ->
            val key = propertyEntry.keyEntry.text
            val value = propertyEntry.valueEntry?.text
            if (value.isNullOrBlank()) {
                entryTypes[key] = ANY

                return@forEach
            }

            EnvFieldType.prioritizedEntries.forEach typeLoop@{ type ->
                val parser = type.parser

                if (parser(value)) {
                    entryTypes[key] = type

                    return@forEach
                }
            }
        }

        return entryTypes
    }

    suspend fun createNewEnvSourceFromTemplate(
        project: Project = this.project,
        envTemplate: VirtualFile,
    ): EnvSource {
        return createMissingSource(
            project = project,
            fileName = ".${dEnvFileType.defaultExtension}",
            file = envTemplate,
            syncSources = { existing, newSource ->
                syncSourceWithTemplate(
                    template = existing,
                    source = newSource
                )
            }
        )
    }

    private suspend fun syncSourceWithTemplate(
        template: PsiFile,
        source: PsiFile,
    ) {
        writeAction {
            val templateKeys = (template as dEnvTemplateFile).keys()
            val formattedKeys = templateKeys.formatAsString()

            (source as dEnvFile).writeContent(
                content = formattedKeys
            )
        }
    }

    private suspend fun createMissingSource(
        project: Project = this.project,
        fileName: String,
        file: VirtualFile,
        syncSources: suspend (existing : PsiFile, newSource: PsiFile) -> Unit
    ): EnvSource {
        val psiManager = PsiManager.getInstance(project)

        val source = writeAction {
            val containerDirectory = file.parent
            val directory = psiManager.findDirectory(containerDirectory)

            directory?.createFile(fileName)
        } ?: throw IllegalStateException("Could not create $file")

        val psiSource = readAction {
            psiManager.findFile(file)!!
        }

        syncSources(
            psiSource,
            source
        )

        return source.virtualFile.toEnvSource(
            project = project
        )
    }

}
