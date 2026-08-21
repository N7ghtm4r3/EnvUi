package com.tecknobit.envui.ide.services

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.enums.EnvFieldType.ANY
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.languages.dEnvFileBase
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvSourceTemplate
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
    var type: EnvFieldType = ANY,
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

    fun upsertFromTemplate(
        source: VirtualFile,
        envSourceTemplate: EnvSourceTemplate,
        onPropertyTypeChange: (String, String) -> Unit,
    ) {
        val properties = mutableMapOf<String, EnvSourcePropertyPreferences>().apply {
            val currentEnvSourcePreferences = retrieveEnvSourcePreferences(
                source = source
            )
            val currentProperties = currentEnvSourcePreferences?.properties ?: emptyMap()

            putAll(currentProperties)
        }

        envSourceTemplate.fields.forEach { field ->
            val key = field.key
            val type = field.type

            var propertyPreferences = properties[key]
            propertyPreferences = syncPropertyPreferenceFromTemplate(
                key = key,
                propertyPreferences = propertyPreferences,
                type = type,
                onTypeChange = onPropertyTypeChange
            )

            properties[key] = propertyPreferences
        }

        storeEnvSourcePreferences(
            source = source,
            envSourcePreferences = EnvSourcePreferences(
                sourcePath = source.path,
                properties = properties
            )
        )
    }

    private fun syncPropertyPreferenceFromTemplate(
        key: String,
        propertyPreferences: EnvSourcePropertyPreferences?,
        type: EnvFieldType,
        onTypeChange: (String, String) -> Unit,
    ): EnvSourcePropertyPreferences {
        var propertyPreferencesSupport: EnvSourcePropertyPreferences?

        if (propertyPreferences != null) {
            val previousType = propertyPreferences.type
            val isTypeChanged = previousType != type && type != ANY
            val initialValue = if (isTypeChanged) "" else propertyPreferences.initialValue
            val currentValue = if (isTypeChanged) "" else propertyPreferences.currentValue

            propertyPreferencesSupport = propertyPreferences.copy(
                type = type,
                initialValue = initialValue,
                currentValue = currentValue
            )

            if (isTypeChanged)
                onTypeChange(key, currentValue)
        } else {
            propertyPreferencesSupport = EnvSourcePropertyPreferences(
                key = key,
                type = type
            )
        }

        return propertyPreferencesSupport
    }

    fun retrievePropertyType(
        source: VirtualFile,
        key: String,
    ): EnvFieldType {
        val envSourcePreferences = retrieveEnvSourcePreferences(
            source = source
        )
        val properties = envSourcePreferences?.properties ?: emptyMap()

        return properties[key]?.type ?: ANY
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
                    -1L
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
                lastUpdateAt = -1L
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
        return envSourcePreferences.copy(
            properties = envSourcePreferences.properties.plus(
                pair = property.keyEntry.text to propertyPreferences
            )
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
        updateState { state ->
            state.upsertEnvSourcePreferences(
                source = source,
                sourcePreferences = envSourcePreferences
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
                if (excludeUnchanged) {
                    val isChanged = property.initialValue != property.currentValue

                    predicate(property) && isChanged
                } else
                    predicate(property)
            }
            envSourcePreference.properties = criticalProperties

            criticalProperties.isNotEmpty()
        }
    }

    fun syncPreferencesFromSource(
        envSource: EnvSource,
    ) {
        val source = envSource.source
        val newPropertiesMap = mapNewProperties(
            psiSource = envSource.psiEnvSource
        )

        val previousPreferences = ensureEnvSourcePreferences(
            source = source
        )
        val previousProperties = previousPreferences.properties

        syncPreferences(
            source = source,
            previousPreferences = previousProperties,
            newPreferences = newPropertiesMap
        )

        removeStaleKeys(
            source = source,
            previousKeys = previousProperties.keys,
            currentKeys = newPropertiesMap.keys
        )
    }

    private fun mapNewProperties(
        psiSource: dEnvFileBase,
    ): Map<String, String> {
        return buildMap {
            psiSource.properties().forEach { property ->
                val key = property.keyEntry.text
                val value = property.valueEntry?.text ?: ""

                put(key, value)
            }
        }
    }

    private fun ensureEnvSourcePreferences(
        source: VirtualFile,
    ): EnvSourcePreferences {
        var preferences = retrieveEnvSourcePreferences(
            source = source
        )

        if (preferences == null) {
            val emptyEnvSourcePreferences = EnvSourcePreferences(
                sourcePath = source.path
            )

            storeEnvSourcePreferences(
                source = source,
                envSourcePreferences = emptyEnvSourcePreferences
            )

            preferences = emptyEnvSourcePreferences
        }

        return preferences
    }

    private fun syncPreferences(
        source: VirtualFile,
        previousPreferences: Map<String, EnvSourcePropertyPreferences>,
        newPreferences: Map<String, String>,
    ) {
        updateState { state ->
            var sourcePreferences = state.preferences[source.path]
            val propertyPreferences = sourcePreferences?.properties?.toMutableMap() ?: return

            newPreferences.forEach { (key, value) ->
                var propertyPrefs = previousPreferences[key] ?: EnvSourcePropertyPreferences()
                val previousInitialValue = propertyPrefs.initialValue

                propertyPrefs = propertyPrefs.copy(
                    key = key,
                    initialValue = previousInitialValue.ifBlank { value },
                    currentValue = value,
                    lastUpdateAt = System.currentTimeMillis()
                )

                propertyPreferences[key] = propertyPrefs
            }
            sourcePreferences = sourcePreferences.copy(
                properties = propertyPreferences
            )

            state.upsertEnvSourcePreferences(
                source = source,
                sourcePreferences = sourcePreferences
            )
        }
    }

    private fun removeStaleKeys(
        source: VirtualFile,
        previousKeys: Set<String>,
        currentKeys: Set<String>,
    ) {
        val staleKeys = previousKeys.minus(currentKeys)
        if (staleKeys.isEmpty())
            return

        deletePreferences(
            source = source,
            propertyKeys = staleKeys
        )
    }

    fun deletePreferences(
        source: VirtualFile,
        propertyKeys: Set<String>,
    ) {
        updateState { state ->
            val sourcePath = source.path
            var sourcePreferences = state.preferences[sourcePath] ?: return@updateState state

            sourcePreferences = sourcePreferences.copy(
                properties = sourcePreferences.properties.minus(propertyKeys)
            )

            state.upsertEnvSourcePreferences(
                source = source,
                sourcePreferences = sourcePreferences
            )
        }
    }

    fun deleteAllSourcePreferences(
        source: VirtualFile,
    ) {
        updateState { state ->
            val key = source.path

            state.copy(
                preferences = state.preferences.minus(key)
            )
        }
    }

    private fun EnvUiState.upsertEnvSourcePreferences(
        source: VirtualFile,
        sourcePreferences: EnvSourcePreferences,
    ) = copy(
        preferences = state.preferences.plus(
            pair = source.path to sourcePreferences
        )
    )

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
