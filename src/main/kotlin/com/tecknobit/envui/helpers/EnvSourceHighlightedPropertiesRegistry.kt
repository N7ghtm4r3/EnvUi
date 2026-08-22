package com.tecknobit.envui.helpers

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.tecknobit.envui.helpers.EnvSourcePreferenceType.CRITICAL
import com.tecknobit.envui.helpers.EnvSourcePreferenceType.RESET_ON_CLOSE
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

/**
 * The `EnvSourcePreferenceType` enum is useful to represent a visual preference applied to an environment property
 *
 * @property displayName The name used to persist the preference type
 *
 * @author N7ghtm4r3 - Tecknobit
 */
enum class EnvSourcePreferenceType(
    val displayName: String
) {

    /**
     * The preference marking a property as critical
     */
    CRITICAL(
        displayName = "is_critical"
    ),

    /**
     * The preference requiring a property value to be reset when the project closes
     */
    RESET_ON_CLOSE(
        displayName = "reset_on_close"
    );

    /**
     * `conflictualPreferences` the preference types incompatible with this type
     */
    val conflictualPreferences: Set<EnvSourcePreferenceType>
        get() = when(this) {
            CRITICAL -> hashSetOf(RESET_ON_CLOSE)
            RESET_ON_CLOSE -> hashSetOf(CRITICAL)
        }

}

/**
 * Map associating environment property keys and preference types with their range highlighters
 *
 * @author N7ghtm4r3 - Tecknobit
 */
private typealias EnvSourceHighlightedPropertyEntry = HashMap<String, HashMap<EnvSourcePreferenceType, RangeHighlighter>>

/**
 * The `EnvSourceHighlightedPropertiesRegistry` object allows to track the active property preference highlighters
 *
 * @author N7ghtm4r3 - Tecknobit
 */
object EnvSourceHighlightedPropertiesRegistry {

    /**
     * `highlightedProperties` the registered highlighters grouped by source path and property key
     */
    private val highlightedProperties = hashMapOf<String, EnvSourceHighlightedPropertyEntry>()

    /**
     * Method used to register the critical marker of an environment property
     *
     * @param envSource The environment source containing the property
     * @param key The key of the property
     * @param highlighter The range highlighter representing the marker
     */
    fun markPropertyAsCritical(
        envSource: EnvSource,
        key: String,
        highlighter: RangeHighlighter
    ) {
        markPropertyAsPrefType(
            envSource = envSource,
            key = key,
            type = CRITICAL,
            highlighter = highlighter
        )
    }

    /**
     * Method used to register the reset-on-close marker of an environment property
     *
     * @param envSource The environment source containing the property
     * @param key The key of the property
     * @param highlighter The range highlighter representing the marker
     */
    fun markPropertyAsResettableOnClose(
        envSource: EnvSource,
        key: String,
        highlighter: RangeHighlighter
    ) {
        markPropertyAsPrefType(
            envSource = envSource,
            key = key,
            type = RESET_ON_CLOSE,
            highlighter = highlighter
        )
    }

    /**
     * Method used to register a preference marker of an environment property
     *
     * @param envSource The environment source containing the property
     * @param key The key of the property
     * @param type The preference type represented by the marker
     * @param highlighter The range highlighter representing the marker
     */
    fun markPropertyAsPrefType(
        envSource: EnvSource,
        key: String,
        type: EnvSourcePreferenceType,
        highlighter: RangeHighlighter
    ) {
        val registryEntryKey = envSource.resolveRegistryEntryKey()
        highlightedProperties.putIfAbsent(registryEntryKey, hashMapOf())

        val envSourcePreferences = highlightedProperties[registryEntryKey]!!
        envSourcePreferences.putIfAbsent(key, hashMapOf())

        val propertyPreferences = envSourcePreferences[key]!!
        propertyPreferences.putIfAbsent(type, highlighter)
    }

    /**
     * Method used to unregister a preference marker from an environment property
     *
     * @param envSource The environment source containing the property
     * @param key The key of the property
     * @param type The preference type to unregister
     */
    fun unmarkPropertyAsPrefType(
        envSource: EnvSource,
        key: String,
        type: EnvSourcePreferenceType
    ) {
        val registryEntryKey = envSource.resolveRegistryEntryKey()
        val envSourcePreferences = highlightedProperties[registryEntryKey]!!
        val propertyPreferences = envSourcePreferences[key]!!

        propertyPreferences.remove(type)
    }

    /**
     * Method used to retrieve the registered preference highlighter of an environment property
     *
     * @param envSource The environment source containing the property
     * @param key The key of the property
     * @param type The preference type represented by the highlighter
     *
     * @return the registered range highlighter, if available, as [RangeHighlighter]
     */
    fun getPropertyHighlighter(
        envSource: EnvSource,
        key: String,
        type: EnvSourcePreferenceType,
    ): RangeHighlighter? {
        val registryEntryKey = envSource.resolveRegistryEntryKey()

        return highlightedProperties[registryEntryKey]
            ?.get(key)
            ?.get(type)
    }

    /**
     * Method used to resolve the registry key of this environment source
     *
     * @return the source registry key as [String]
     */
    private fun EnvSource.resolveRegistryEntryKey(): String {
        return path
    }

}