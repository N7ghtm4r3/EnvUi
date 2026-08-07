package com.tecknobit.envui.helpers

import com.intellij.openapi.vfs.VirtualFile

data class EnvSourcePreferences(
    val source: VirtualFile,
    val properties: List<EnvSourcePropertyPreferences>
)

data class EnvSourcePropertyPreferences(
    val key: String,
    val isCritical: Boolean,
    val requireResetOnClose: Boolean,
)

object EnvSourcePreferencesManager {

    fun retrieveEnvSourcePreferences(
        source: VirtualFile
    ): EnvSourcePreferences {
        return EnvSourcePreferences(
            source = source,
            properties = listOf(
                EnvSourcePropertyPreferences(
                    key = "p4",
                    isCritical = true,
                    requireResetOnClose = true
                )
            )
        )
    }

}