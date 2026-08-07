package com.tecknobit.envui.util
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.tecknobit.envui.ide.languages.dEnvFileBase
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvTemplateField
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
}

fun VirtualFile.toEnvSource(
    project: Project,
    template: VirtualFile? = null
): EnvSource {
    val psiManager = PsiManager.getInstance(project)

    return EnvSource(
        project = project,
        source = this,
        module = ModuleUtilCore.findModuleForFile(this, project),
        _psiSource = psiManager.findFile(this)!!,
        _templateSource = if(template != null)
            psiManager.findFile(template)
        else
            null
    )
}