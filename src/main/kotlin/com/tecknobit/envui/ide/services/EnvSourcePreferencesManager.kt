package com.tecknobit.envui.ide.services

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

data class EnvUiState(
    var preferences: Map<String, EnvSourcePreferences> = mapOf()
)

data class EnvSourcePreferences(
    var sourcePath: String = "",
    var properties: Map<String, EnvSourcePropertyPreferences> = mapOf()
)

data class EnvSourcePropertyPreferences(
    var key: String = "",
    var isCritical: Boolean = false,
    var requireResetOnClose: Boolean = false,
)

@Service(Service.Level.PROJECT)
@State(
    name = "EnvUiPreferences",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class EnvSourcePreferencesManager : SerializablePersistentStateComponent<EnvUiState>(
    state = EnvUiState()
) {

    fun retrieveEnvSourcePreferences(
        source: VirtualFile
    ): EnvSourcePreferences? {
        return state.preferences[source.path]
    }

    fun retrievePropertyPreferences(
        source: VirtualFile,
        property: Property
    ): EnvSourcePropertyPreferences {
        val envSourcePreference = retrieveEnvSourcePreferences(
            source = source
        )
        val propertyKey = property.keyEntry.text
        val defaultPropertyPreferences = EnvSourcePropertyPreferences(
            key = propertyKey
        )
        if(envSourcePreference == null)
            return defaultPropertyPreferences

        val propertyPreferences = envSourcePreference.properties[propertyKey]
        return propertyPreferences ?: defaultPropertyPreferences
    }

    fun setPropertyCriticality(
        source: VirtualFile,
        property: Property,
        isCritical: Boolean
    ) {
        workOnPropertyPreferences(
            source = source,
            property = property
        ) { propertyPreferences ->
            propertyPreferences.copy(
                isCritical = isCritical
            )
        }
    }

    fun setPropertyResetOnClose(
        source: VirtualFile,
        property: Property,
        resetOnClose: Boolean
    ) {
        workOnPropertyPreferences(
            source = source,
            property = property
        ) { propertyPreferences ->
            propertyPreferences.copy(
                requireResetOnClose = resetOnClose
            )
        }
    }

    private inline fun workOnPropertyPreferences(
        source: VirtualFile,
        property: Property,
        onWork: (EnvSourcePropertyPreferences) -> EnvSourcePropertyPreferences
    ) {
        var envSourcePreferences = retrieveEnvSourcePreferences(
            source = source
        )
        if(envSourcePreferences == null) {
            envSourcePreferences = storeNewEnvSourcePreferences(
                source = source
            )
        }

        val propertyPreferences = retrievePropertyPreferences(
            source = source,
            property = property
        )

        val newPrefs = onWork(propertyPreferences)
        envSourcePreferences = upsertPropertyPreferences(
            envSourcePreferences = envSourcePreferences,
            property = property,
            propertyPreferences = newPrefs
        )

        storeEnvSourcePreferences(
            source = source,
            envSourcePreferences = envSourcePreferences
        )
    }

    private fun upsertPropertyPreferences(
        envSourcePreferences: EnvSourcePreferences,
        property: Property,
        propertyPreferences: EnvSourcePropertyPreferences
    ): EnvSourcePreferences {
        val propertyPreferencesEntry = Pair(property.keyEntry.text, propertyPreferences)

        return envSourcePreferences.copy(
            properties = envSourcePreferences.properties + propertyPreferencesEntry
        )
    }

    private fun storeNewEnvSourcePreferences(
        source: VirtualFile
    ): EnvSourcePreferences {
        val envSourcePreferences = EnvSourcePreferences(
            sourcePath = source.path
        )

        storeEnvSourcePreferences(
            source = source,
            envSourcePreferences = envSourcePreferences
        )

        return envSourcePreferences
    }

    private fun storeEnvSourcePreferences(
        source: VirtualFile,
        envSourcePreferences: EnvSourcePreferences
    ) {
        val envSourceEntry = Pair(source.path, envSourcePreferences)

        updateState { state ->
            state.copy(
                preferences = state.preferences + envSourceEntry
            )
        }
    }

}

inline fun <T> EnvSource.useEnvSourcePreferencesManager(
    crossinline usage: EnvSourcePreferencesManager.() -> T
): T {
    return this.project.useEnvSourcePreferencesManager(
        usage = usage
    )
}

inline fun <T> Project.useEnvSourcePreferencesManager(
    crossinline usage: EnvSourcePreferencesManager.() -> T
): T {
    val preferencesManager = service<EnvSourcePreferencesManager>()

    return usage(preferencesManager)
}
