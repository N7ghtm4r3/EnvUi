package com.tecknobit.envui.helpers

import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.envfile.Property

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

            )
        )
    }

    fun retrievePropertyPreferences(
        source: VirtualFile,
        property: Property
    ): EnvSourcePropertyPreferences {
        return EnvSourcePropertyPreferences(
            key = "p4",
            isCritical = false,
            requireResetOnClose = false
        )
    }

}