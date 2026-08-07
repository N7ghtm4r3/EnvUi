package com.tecknobit.envui.helpers

import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.envfile.Property
import kotlin.random.Random

enum class EnvSourcePreferencesType(
    val displayName: String
) {

    CRITICAL(
        displayName = "is_critical"
    ),

    RESET_ON_CLOSE(
        displayName = "reset_on_close"
    )

}

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

    fun retrievePropertyPreferences(
        source: VirtualFile,
        property: Property
    ): EnvSourcePropertyPreferences {
        return EnvSourcePropertyPreferences(
            key = "p4",
            isCritical = Random.nextBoolean(),
            requireResetOnClose = Random.nextBoolean()
        )
    }

}