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
    var initialValue: String = "",
    var currentValue: String = initialValue,
    var lastUpdateAt: Long = -1L,
    var isChanged: Boolean = initialValue != currentValue,
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
        return retrievePropertyPreferences(
            source = source,
            key = property.keyEntry.text,
            value = property.valueEntry?.text
        )
    }

    fun retrievePropertyPreferences(
        source: VirtualFile,
        key: String,
        value: String? = ""
    ): EnvSourcePropertyPreferences {
        val envSourcePreference = retrieveEnvSourcePreferences(
            source = source
        )
        val defaultPropertyPreferences = EnvSourcePropertyPreferences(
            key = key,
            initialValue = value ?: ""
        )
        if(envSourcePreference == null)
            return defaultPropertyPreferences

        val propertyPreferences = envSourcePreference.properties[key]
        return propertyPreferences ?: defaultPropertyPreferences
    }

    fun setPropertyValue(
        source: VirtualFile,
        property: Property,
        value: String
    ) {
        setPropertyPreference(
            source = source,
            property = property
        ) { propertyPreferences ->
            var currentInitialValue = propertyPreferences.initialValue
            if(currentInitialValue.isBlank())
                currentInitialValue = value
            val isChanged = currentInitialValue != value

            propertyPreferences.copy(
                initialValue = currentInitialValue,
                currentValue = value,
                lastUpdateAt =  if (isChanged)
                    System.currentTimeMillis()
                else
                    -1L,
                isChanged = isChanged
            )
        }
    }

    fun acceptNewPropertyValue(
        source: VirtualFile,
        property: Property
    ) {
        setPropertyPreference(
            source = source,
            property = property
        ) { propertyPreferences ->
            val newValue = propertyPreferences.currentValue

            propertyPreferences.copy(
                initialValue = newValue,
                currentValue = newValue,
                lastUpdateAt = -1L,
                isChanged = false
            )
        }
    }

    fun setPropertyCriticality(
        source: VirtualFile,
        property: Property,
        isCritical: Boolean
    ) {
         setPropertyPreference(
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
        setPropertyPreference(
            source = source,
            property = property
        ) { propertyPreferences ->
            propertyPreferences.copy(
                requireResetOnClose = resetOnClose
            )
        }
    }

    fun setPropertyPreference(
        source: VirtualFile,
        property: Property,
        onSet: (EnvSourcePropertyPreferences) -> EnvSourcePropertyPreferences
    ) {
        workOnPropertyPreferences(
            source = source,
            property = property
        ) { propertyPreferences ->
            onSet(propertyPreferences)
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

    fun retrieveAllEnvSourcePreferences(): Map<String, EnvSourcePreferences> {
        return state.preferences.mapValues { (_, preferences) ->
            preferences.copy(
                properties = preferences.properties.mapValues { (_, property) ->
                    property.copy()
                }
            )
        }
    }

    fun retrieveAllCriticalEnvSourcePreferences(
        excludeUnchanged: Boolean = true
    ): List<EnvSourcePreferences> {
        return retrieveAllEnvSourcePreferences(
            excludeUnchanged = excludeUnchanged,
            predicate = { propertyPreferences ->
                propertyPreferences.isCritical
            }
        )
    }

    fun retrieveAllResettableOnCloseEnvSourcePreferences(
        excludeUnchanged: Boolean = true
    ): List<EnvSourcePreferences> {
        return retrieveAllEnvSourcePreferences(
            excludeUnchanged = excludeUnchanged,
            predicate = { propertyPreferences ->
                propertyPreferences.requireResetOnClose
            }
        )
    }

    private inline fun retrieveAllEnvSourcePreferences(
        predicate: (EnvSourcePropertyPreferences) -> Boolean,
        excludeUnchanged: Boolean = true
    ): List<EnvSourcePreferences> {
        val envSourcePreferences = retrieveAllEnvSourcePreferences()

        return envSourcePreferences.values.filter { envSourcePreference ->
            val criticalProperties = envSourcePreference.properties.filter { (_, property) ->
                if(excludeUnchanged)
                    predicate(property) && property.isChanged
                else
                    predicate(property)
            }

            envSourcePreference.properties = criticalProperties

            criticalProperties.isNotEmpty()
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
