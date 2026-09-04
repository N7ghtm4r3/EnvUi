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

/**
 * The `EnvUiState` class is useful to persist the environment source preferences of a project
 *
 * @property preferences The environment source preferences indexed by source path
 *
 * @author N7ghtm4r3 - Tecknobit
 */
data class EnvUiState(
    var preferences: Map<String, EnvSourcePreferences> = mapOf()
)

/**
 * The `EnvSourcePreferences` class is useful to persist the preferences of an environment source
 *
 * @property sourcePath The path of the environment source
 * @property properties The property preferences indexed by key
 *
 * @author N7ghtm4r3 - Tecknobit
 */
data class EnvSourcePreferences(
    var sourcePath: String = "",
    var properties: Map<String, EnvSourcePropertyPreferences> = mapOf()
)

/**
 * The `EnvSourcePropertyPreferences` class is useful to persist the state and behavior of an environment property
 *
 * @property key The key of the environment property
 * @property isCritical Whether the property is marked as critical
 * @property requireResetOnClose Whether the property must be reset when the project closes
 * @property initialValue The accepted value of the property
 * @property currentValue The current value of the property
 * @property lastUpdateAt The timestamp of the latest value change, or `-1` when unchanged
 * @property type The expected value type of the property
 *
 * @author N7ghtm4r3 - Tecknobit
 */
data class EnvSourcePropertyPreferences(
    var key: String = "",
    var isCritical: Boolean = false,
    var requireResetOnClose: Boolean = false,
    var initialValue: String = "",
    var currentValue: String = initialValue,
    var lastUpdateAt: Long = -1L,
    var type: EnvFieldType = ANY,
)

/**
 * The `EnvSourcePreferencesManager` class is useful to persist and synchronize environment source preferences
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Service(Service.Level.PROJECT)
@State(
    name = "EnvUiPreferences",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class EnvSourcePreferencesManager : SerializablePersistentStateComponent<EnvUiState>(
    state = EnvUiState()
) {

    /**
     * Method used to retrieve the stored preferences of an environment source
     *
     * @param source The environment source file
     *
     * @return the stored source preferences, if available, as [EnvSourcePreferences]
     */
    fun retrieveEnvSourcePreferences(
        source: VirtualFile
    ): EnvSourcePreferences? {
        return state.preferences[source.path]
    }

    /**
     * Method used to retrieve the preferences of an environment property
     *
     * @param source The environment source file
     * @param property The environment property
     *
     * @return the stored or default property preferences as [EnvSourcePropertyPreferences]
     */
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

    /**
     * Method used to retrieve the preferences of an environment property by key
     *
     * @param source The environment source file
     * @param key The key of the property
     * @param value The optional value used to initialize missing preferences
     *
     * @return the stored or default property preferences as [EnvSourcePropertyPreferences]
     */
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

    /**
     * Method used to synchronize the property preferences of a source with an environment template
     *
     * @param source The environment source file
     * @param envSourceTemplate The template containing the expected fields and types
     * @param onPropertyTypeChange The callback invoked with a key and cleared value when its type changes
     */
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

    /**
     * Method used to synchronize one property preference with its template field type
     *
     * @param key The key of the property
     * @param propertyPreferences The optional existing property preferences
     * @param type The type declared by the template
     * @param onTypeChange The callback invoked when a concrete type change clears the value
     *
     * @return the synchronized property preferences as [EnvSourcePropertyPreferences]
     */
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

    /**
     * Method used to retrieve the stored type of an environment property
     *
     * @param source The environment source file
     * @param key The key of the property
     *
     * @return the stored property type or [ANY] as [EnvFieldType]
     */
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

    fun saveBatchPropertyTypes(
        source: VirtualFile,
        propertyTypes: Map<String, EnvFieldType>,
    ) {
        var envSourcePreferences = ensureEnvSourcePreferences(
            source = source
        )

        propertyTypes.forEach { (key, type) ->
            envSourcePreferences = upsertPropertyPreferences(
                envSourcePreferences = envSourcePreferences,
                propertyKey = key,
                propertyPreferences = EnvSourcePropertyPreferences(
                    key = key,
                    type = type
                )
            )
        }

        storeEnvSourcePreferences(
            source = source,
            envSourcePreferences = envSourcePreferences
        )
    }

    /**
     * Method used to store the current value and change timestamp of an environment property
     *
     * @param source The environment source file
     * @param property The environment property
     * @param value The current property value
     */
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

    /**
     * Method used to accept the current value of an environment property as its initial value
     *
     * @param source The environment source file
     * @param property The environment property
     */
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

    /**
     * Method used to set whether an environment property is critical
     *
     * @param source The environment source file
     * @param property The environment property
     * @param isCritical Whether the property is critical
     */
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

    /**
     * Method used to set whether an environment property must be reset when the project closes
     *
     * @param source The environment source file
     * @param property The environment property
     * @param resetOnClose Whether the property must be reset on close
     */
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

    /**
     * Method used to transform and store the preferences of an environment property
     *
     * @param source The environment source file
     * @param property The environment property
     * @param onSet The transformation applied to the current property preferences
     */
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

    /**
     * Method used to ensure, transform, and persist the preferences of an environment property
     *
     * @param source The environment source file
     * @param property The environment property
     * @param onWork The transformation applied to the property preferences
     */
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

    /**
     * Method used to insert or replace property preferences in source preferences
     *
     * @param envSourcePreferences The source preferences to update
     * @param property The environment property used as map key
     * @param propertyPreferences The preferences to store
     *
     * @return the updated source preferences as [EnvSourcePreferences]
     */
    private fun upsertPropertyPreferences(
        envSourcePreferences: EnvSourcePreferences,
        property: Property,
        propertyPreferences: EnvSourcePropertyPreferences
    ): EnvSourcePreferences {
        return upsertPropertyPreferences(
            envSourcePreferences = envSourcePreferences,
            propertyKey = property.keyEntry.text,
            propertyPreferences = propertyPreferences
        )
    }

    private fun upsertPropertyPreferences(
        envSourcePreferences: EnvSourcePreferences,
        propertyKey: String,
        propertyPreferences: EnvSourcePropertyPreferences,
    ): EnvSourcePreferences {
        return envSourcePreferences.copy(
            properties = envSourcePreferences.properties.plus(
                pair = propertyKey to propertyPreferences
            )
        )
    }

    /**
     * Method used to create and store empty preferences for an environment source
     *
     * @param source The environment source file
     *
     * @return the created source preferences as [EnvSourcePreferences]
     */
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

    /**
     * Method used to store the preferences of an environment source
     *
     * @param source The environment source file
     * @param envSourcePreferences The source preferences to store
     */
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

    /**
     * Method used to retrieve defensive copies of all environment source preferences
     *
     * @return the copied source preferences indexed by path as [Map]
     */
    fun retrieveAllEnvSourcePreferences(): Map<String, EnvSourcePreferences> {
        return state.preferences.mapValues { (_, preferences) ->
            preferences.copy(
                properties = preferences.properties.mapValues { (_, property) ->
                    property.copy()
                }
            )
        }
    }

    /**
     * Method used to retrieve source preferences containing critical properties
     *
     * @param excludeUnchanged Whether unchanged critical properties must be excluded
     *
     * @return the source preferences containing matching properties as [List] of [EnvSourcePreferences]
     */
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

    /**
     * Method used to retrieve source preferences containing properties resettable on close
     *
     * @param excludeUnchanged Whether unchanged resettable properties must be excluded
     *
     * @return the source preferences containing matching properties as [List] of [EnvSourcePreferences]
     */
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

    /**
     * Method used to retrieve source preferences whose properties satisfy a predicate
     *
     * @param predicate The condition applied to each property preference
     * @param excludeUnchanged Whether unchanged properties must be excluded
     *
     * @return the source preferences containing matching properties as [List] of [EnvSourcePreferences]
     */
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

    /**
     * Method used to synchronize stored preferences with the current properties of an environment source
     *
     * @param envSource The environment source to synchronize
     */
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

    /**
     * Method used to map the keys and values declared by an environment `PSI` source
     *
     * @param psiSource The environment `PSI` source to map
     *
     * @return the property values indexed by key as [Map]
     */
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

    /**
     * Method used to retrieve or create the preferences of an environment source
     *
     * @param source The environment source file
     *
     * @return the ensured source preferences as [EnvSourcePreferences]
     */
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

    /**
     * Method used to synchronize stored property preferences with current source values
     *
     * @param source The environment source file
     * @param previousPreferences The previously stored property preferences
     * @param newPreferences The current source values indexed by key
     */
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

    /**
     * Method used to remove preferences for keys no longer present in an environment source
     *
     * @param source The environment source file
     * @param previousKeys The previously stored property keys
     * @param currentKeys The current property keys
     */
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

    /**
     * Method used to delete selected property preferences from an environment source
     *
     * @param source The environment source file
     * @param propertyKeys The keys whose preferences must be deleted
     */
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

    /**
     * Method used to delete all preferences stored for an environment source
     *
     * @param source The environment source file
     */
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

    /**
     * Method used to insert or replace source preferences in this persistent state
     *
     * @param source The environment source file used as map key
     * @param sourcePreferences The source preferences to store
     *
     * @return the updated persistent state as [EnvUiState]
     */
    private fun EnvUiState.upsertEnvSourcePreferences(
        source: VirtualFile,
        sourcePreferences: EnvSourcePreferences,
    ) = copy(
        preferences = state.preferences.plus(
            pair = source.path to sourcePreferences
        )
    )

}

/**
 * Method used to execute an operation with the project preference manager of this environment source
 *
 * @param T The result type of the operation
 * @param usage The operation to execute with the preference manager
 *
 * @return the operation result as [T]
 */
inline fun <T> EnvSource.useEnvSourcePreferencesManager(
    crossinline usage: EnvSourcePreferencesManager.() -> T
): T {
    return this.project.useEnvSourcePreferencesManager(
        usage = usage
    )
}

/**
 * Method used to execute an operation with the environment source preference manager of this project
 *
 * @param T The result type of the operation
 * @param usage The operation to execute with the preference manager
 *
 * @return the operation result as [T]
 */
inline fun <T> Project.useEnvSourcePreferencesManager(
    crossinline usage: EnvSourcePreferencesManager.() -> T
): T {
    val preferencesManager = service<EnvSourcePreferencesManager>()

    return usage(preferencesManager)
}
