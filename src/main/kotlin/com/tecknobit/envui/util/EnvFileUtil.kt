package com.tecknobit.envui.util

import com.tecknobit.envui.ide.dEnvFileBase
import com.tecknobit.envui.ide.envfile.dEnvFile
import com.tecknobit.envui.ide.envfiletemplate.dEnvTemplateFile
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvTemplateField

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
    val templateKeys = template.fields
    val treadAsNegativeOffset = currentSourceProperties.size > templateKeys.size
    val removedKeys = template.removedFields
    var mappingOffset = 0

    var content = buildString {
        currentSourceProperties.forEachIndexed { index, field ->
            val existingKey = field.keyEntry.text

            if(!removedKeys.contains(existingKey)) {
                var entry = "${templateKeys[index + mappingOffset].key}="
                val valueEntry  = field.valueEntry

                valueEntry?.let {
                    entry += valueEntry.text
                }

                append(entry)
                append("\n")
                mappingOffset = 0
            } else {
                if(treadAsNegativeOffset)
                    mappingOffset--
                else
                    mappingOffset++
            }
        }
    }

    //TODO: START FROM CONSUMED KEYS
    content += templateKeys.joinToString(
        separator = "\n",
        transform = { key -> "${key.key}=" }
    )

    writeContent(
        content = content
    )
}