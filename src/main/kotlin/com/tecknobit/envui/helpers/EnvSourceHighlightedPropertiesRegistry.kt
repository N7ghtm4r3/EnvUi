package com.tecknobit.envui.helpers

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.tecknobit.envui.helpers.EnvSourcePreferenceType.CRITICAL
import com.tecknobit.envui.helpers.EnvSourcePreferenceType.RESET_ON_CLOSE
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

enum class EnvSourcePreferenceType(
    val displayName: String
) {

    CRITICAL(
        displayName = "is_critical"
    ),

    RESET_ON_CLOSE(
        displayName = "reset_on_close"
    );

    val conflictualPreferences: Set<EnvSourcePreferenceType>
        get() = when(this) {
            CRITICAL -> hashSetOf(RESET_ON_CLOSE)
            RESET_ON_CLOSE -> hashSetOf(CRITICAL)
        }

}

private typealias EnvSourceHighlightedPropertyEntry = HashMap<String, HashMap<EnvSourcePreferenceType, RangeHighlighter>>

object EnvSourceHighlightedPropertiesRegistry {

    private val highlightedProperties = hashMapOf<String, EnvSourceHighlightedPropertyEntry>()

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

    private fun EnvSource.resolveRegistryEntryKey(): String {
        return path
    }

}