package com.tecknobit.envui.utils
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
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

fun VirtualFile.toEnvSource(
    project: Project,
    template: VirtualFile? = null,
    resolveModule: Boolean = true
): EnvSource {
    return runReadAction {
        val psiManager = PsiManager.getInstance(project)
        val module = if (resolveModule)
            ModuleUtilCore.findModuleForFile(this, project)
        else
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

private fun VirtualFile.resolveEnvSource(
    project: Project,
): PsiFile? {
    return resolveEnvSourceFile(
        project = project,
        fileName = ENV_FILENAME
    )
}

private fun VirtualFile.resolveEnvSourceTemplate(
    project: Project,
): PsiFile? {
    return resolveEnvSourceFile(
        project = project,
        fileName = ENV_TEMPLATE_FILENAME
    )
}

private fun VirtualFile.resolveEnvSourceFile(
    project: Project,
    fileName: String,
): PsiFile? {
    val psiManager = PsiManager.getInstance(project)
    val templateVirtualFile = parent.findChild(fileName) ?: return null

    return psiManager.findFile(templateVirtualFile)
}