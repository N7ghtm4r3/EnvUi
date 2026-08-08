package com.tecknobit.envui.helpers

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.CRITICAL
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.RESET_ON_CLOSE
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

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

private typealias EnvSourceHighlightedPropertyEntry = HashMap<String, HashMap<EnvSourcePreferencesType, RangeHighlighter>>

object EnvSourceHighlightedPropertiesRegistry {

    private val highlightedProperties = hashMapOf<String, EnvSourceHighlightedPropertyEntry>()

    fun isPropertyMarkedAsCritical(
        envSource: EnvSource,
        key: String
    ): Boolean {
        return checkPropertyPrefType(
            envSource = envSource,
            key = key,
            type = CRITICAL
        )
    }

    fun isPropertyMarkedAsResettableOnClose(
        envSource: EnvSource,
        key: String
    ): Boolean {
        return checkPropertyPrefType(
            envSource = envSource,
            key = key,
            type = RESET_ON_CLOSE
        )
    }

    private fun checkPropertyPrefType(
        envSource: EnvSource,
        key: String,
        type: EnvSourcePreferencesType
    ): Boolean {
        val registryEntryKey = envSource.resolveRegistryEntryKey()
        val envSourcePreferences = highlightedProperties[registryEntryKey] ?: return false
        val propertyPreferences = envSourcePreferences[key]  ?: return false

        return propertyPreferences[type] != null
    }

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
        type: EnvSourcePreferencesType,
        highlighter: RangeHighlighter
    ) {
        val registryEntryKey = envSource.resolveRegistryEntryKey()
        highlightedProperties.putIfAbsent(registryEntryKey, hashMapOf())

        val envSourcePreferences = highlightedProperties[registryEntryKey]!!
        envSourcePreferences.putIfAbsent(key, hashMapOf())

        val propertyPreferences = envSourcePreferences[key]!!
        propertyPreferences[type] = highlighter
    }

    fun unmarkPropertyAsCritical(
        envSource: EnvSource,
        key: String
    ) {
        unmarkPropertyAsPrefType(
            envSource = envSource,
            key = key,
            type = CRITICAL
        )
    }

    fun unmarkPropertyAsResettableOnClose(
        envSource: EnvSource,
        key: String
    ) {
        unmarkPropertyAsPrefType(
            envSource = envSource,
            key = key,
            type = RESET_ON_CLOSE
        )
    }

    fun unmarkPropertyAsPrefType(
        envSource: EnvSource,
        key: String,
        type: EnvSourcePreferencesType
    ) {
        val registryEntryKey = envSource.resolveRegistryEntryKey()
        val envSourcePreferences = highlightedProperties[registryEntryKey]!!
        val propertyPreferences = envSourcePreferences[key]!!

        propertyPreferences.remove(type)
    }

    fun getPropertyHighlighter(
        envSource: EnvSource,
        key: String,
        type: EnvSourcePreferencesType,
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