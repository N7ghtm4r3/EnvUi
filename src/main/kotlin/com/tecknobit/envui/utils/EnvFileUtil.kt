package com.tecknobit.envui.utils
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.tecknobit.envui.ide.languages.dEnvFileBase
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFile.Companion.ENV_FILENAME
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile.Companion.ENV_TEMPLATE_FILENAME
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

/**
 * Method used to rewrite this environment file with its current keys and empty values
 */
fun dEnvFileBase.writeKeys() {
    val keys = keys()
    val formattedKeys = keys.joinToString(
        separator = "\n",
        transform = { key -> "${key.text}=" }
    )

    writeContent(
        content = formattedKeys
    )
}

/**
 * Method used to replace the keys of this environment template with the specified fields
 *
 * @param templateKeys The template fields containing the keys to write
 */
fun dEnvTemplateFile.updateKeysFromTemplate(
    templateKeys: Collection<EnvTemplateField>
) {
    val formattedKeys = templateKeys.joinToString(
        separator = "\n",
        transform = { key -> "${key.key}=" }
    )

    writeContent(
        content = formattedKeys
    )
}

/**
 * Method used to update this environment source from the specified template
 *
 * Existing values are retained for preserved fields and preferences for removed fields are deleted
 *
 * @param template The template to apply to the environment source
 */
fun dEnvFile.updateSourceFromTemplate(
    template: EnvSourceTemplate
) {
    val currentSourceProperties = properties()
    val templateFields = template.fields
    val removedKeys = template.removedFields
    var mappingOffset = 0
    val updatedKeys = hashSetOf<String>()

    var content = buildString {
        currentSourceProperties.forEachIndexed { index, field ->
            val existingKey = field.keyEntry.text

            if(!removedKeys.contains(existingKey)) {
                val entryKey = templateFields[index + mappingOffset].key
                var entry = "${entryKey}="
                val valueEntry  = field.valueEntry

                valueEntry?.let {
                    entry += valueEntry.text
                }

                append(entry)
                append("\n")
                updatedKeys.add(entryKey)
            } else
                mappingOffset--
        }
    }

    content += buildString {
        templateFields.forEach { templateField ->
            val key = templateField.key

            if(!updatedKeys.contains(key)) {
                append("${key}=")
                append("\n")
            }
        }
    }

    writeContent(
        content = content
    )

    project.useEnvSourcePreferencesManager {
        deletePreferences(
            source = this@updateSourceFromTemplate.virtualFile,
            propertyKeys = removedKeys
        )
    }
}

/**
 * Method used to convert this virtual environment file into its source model
 *
 * @param project The project containing the virtual file
 * @param template The optional template associated with the source
 * @param resolveModule Whether the containing module must be resolved
 *
 * @return the resolved environment source as [EnvSource]
 */
fun VirtualFile.toEnvSource(
    project: Project,
    template: VirtualFile? = null,
    resolveModule: Boolean = true
): EnvSource {
    return runReadAction {
        val psiManager = PsiManager.getInstance(project)
        val module = if (resolveModule) {
            val projectRootManager = ProjectRootManager.getInstance(project)

            projectRootManager.fileIndex.getModuleForFile(this)
        } else
            null

        if (isEnvFile()) {
            resolveFromSource(
                project = project,
                psiManager = psiManager,
                template = template,
                module = module
            )
        } else {
            resolveFromTemplate(
                project = project,
                psiManager = psiManager,
                module = module
            )
        }
    }
}

/**
 * Method used to resolve this environment file as a source model
 *
 * @param project The project containing the source
 * @param psiManager The manager used to resolve the `PSI` files
 * @param template The optional template associated with the source
 * @param module The optional module containing the source
 *
 * @return the resolved environment source as [EnvSource]
 */
private fun VirtualFile.resolveFromSource(
    project: Project,
    psiManager: PsiManager,
    template: VirtualFile? = null,
    module: Module?,
): EnvSource {
    val templateSource = if (template != null)
        psiManager.findFile(template)
    else {
        resolveEnvSourceTemplate(
            project = project
        )
    }

    return EnvSource(
        project = project,
        source = this,
        module = module,
        _psiSource = psiManager.findFile(this)!!,
        _templateSource = templateSource,
        isResolvedFromTemplate = false
    )
}


/**
 * Method used to resolve this environment template as a source model
 *
 * @param project The project containing the template
 * @param psiManager The manager used to resolve the `PSI` files
 * @param module The optional module containing the template
 *
 * @return the resolved environment source as [EnvSource]
 */
private fun VirtualFile.resolveFromTemplate(
    project: Project,
    psiManager: PsiManager,
    module: Module?,
): EnvSource {
    val sourcePsiFile = resolveEnvSource(
        project = project
    )

    return EnvSource(
        project = project,
        source = sourcePsiFile?.virtualFile!!,
        module = module,
        _psiSource = sourcePsiFile,
        _templateSource = psiManager.findFile(this)!!,
        isResolvedFromTemplate = true
    )
}

/**
 * Method used to resolve the environment source related to this virtual file
 *
 * @param project The project containing the virtual file
 *
 * @return the related environment source `PSI` file, if available, as [PsiFile]
 */
private fun VirtualFile.resolveEnvSource(
    project: Project,
): PsiFile? {
    return resolveEnvSourceFile(
        project = project,
        fileName = ENV_FILENAME
    )
}

/**
 * Method used to resolve the environment template related to this virtual file
 *
 * @param project The project containing the virtual file
 *
 * @return the related environment template `PSI` file, if available, as [PsiFile]
 */
private fun VirtualFile.resolveEnvSourceTemplate(
    project: Project,
): PsiFile? {
    return resolveEnvSourceFile(
        project = project,
        fileName = ENV_TEMPLATE_FILENAME
    )
}

/**
 * Method used to resolve a sibling environment file by name
 *
 * @param project The project containing the virtual file
 * @param fileName The name of the sibling file to resolve
 *
 * @return the related `PSI` file, if available, as [PsiFile]
 */
private fun VirtualFile.resolveEnvSourceFile(
    project: Project,
    fileName: String,
): PsiFile? {
    val psiManager = PsiManager.getInstance(project)
    val templateVirtualFile = parent.findChild(fileName) ?: return null

    return psiManager.findFile(templateVirtualFile)
}