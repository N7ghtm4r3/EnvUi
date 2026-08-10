package com.tecknobit.envui.ide.services

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

data class EnvUiState(
    val preferences: MutableList<EnvSourcePreferences> = mutableListOf()
)

data class EnvSourcePreferences(
    val sourcePath: String,
    val properties: List<EnvSourcePropertyPreferences>
)

data class EnvSourcePropertyPreferences(
    val key: String,
    var isCritical: Boolean = false,
    var requireResetOnClose: Boolean = false,
)

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
            preference.sourcePath == source.path
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

    fun setPropertyCriticality(
        source: VirtualFile,
        property: Property,
        isCritical: Boolean
    ) {
        val propertyPreferences = retrievePropertyPreferences(
            source = source,
            property = property
        )

        propertyPreferences.isCritical = isCritical
    }

    fun setPropertyResetOnClose(
        source: VirtualFile,
        property: Property,
        resetOnClose: Boolean
    ) {
        val propertyPreferences = retrievePropertyPreferences(
            source = source,
            property = property
        )

        propertyPreferences.requireResetOnClose = resetOnClose
    }

    override fun getState(): EnvUiState {
        return currentState
    }

    override fun loadState(state: EnvUiState) {
        currentState = state
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
