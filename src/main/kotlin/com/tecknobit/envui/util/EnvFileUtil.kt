package com.tecknobit.envui.util

import com.tecknobit.envui.ide.envfile.KeyEntry
import com.tecknobit.envui.ide.envfiletemplate.dEnvTemplateFile

fun dEnvTemplateFile.writeKeys(
    keys: Collection<KeyEntry>
) {
    val formattedKeys = keys.joinToString(
        separator = "\n"
    ) { key -> "${key.text}=" }

    writeContent(
        content = formattedKeys
    )
}