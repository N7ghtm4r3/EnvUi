package com.tecknobit.envui.ide.services

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.envfile.Property

data class EnvUiState(
    val preferences: MutableList<EnvSourcePreferences> = mutableListOf()
)

data class EnvSourcePreferences(
    val source: VirtualFile,
    val properties: List<EnvSourcePropertyPreferences>
)

data class EnvSourcePropertyPreferences(
    val key: String,
    val isCritical: Boolean = false,
    val requireResetOnClose: Boolean = false,
)

inline fun Project.useEnvSourcePreferencesManager(
    crossinline usage: (EnvSourcePreferencesManager) -> Unit
) {
    val preferencesManager = service<EnvSourcePreferencesManager>()

    usage(preferencesManager)
}

@Service(Service.Level.PROJECT)
@State(
    name = "EnvUiPreferences",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class EnvSourcePreferencesManager : PersistentStateComponent<EnvUiState> {

    private var currentState = EnvUiState()

    fun retrieveEnvSourcePreferences(
        source: VirtualFile
    ): EnvSourcePreferences? {
        return currentState.preferences.firstOrNull { preference ->
            preference.source.path == source.path
        }
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

        val propertyPreferences = envSourcePreference.properties.firstOrNull { storedProperty ->
            propertyKey == storedProperty.key
        }
        return propertyPreferences ?: defaultPropertyPreferences
    }

    override fun getState(): EnvUiState {
        return currentState
    }

    override fun loadState(p0: EnvUiState) {
        currentState = state
    }

}