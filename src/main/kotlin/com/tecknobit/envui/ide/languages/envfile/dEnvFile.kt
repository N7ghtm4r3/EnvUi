package com.tecknobit.envui.ide.languages.envfile

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.helpers.EnvSourceHighlightedPropertiesRegistry
import com.tecknobit.envui.helpers.EnvSourcePreferenceType
import com.tecknobit.envui.helpers.EnvSourcePreferenceType.CRITICAL
import com.tecknobit.envui.helpers.EnvSourcePreferenceType.RESET_ON_CLOSE
import com.tecknobit.envui.ide.envfile.EnvGeneratedTypes
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.highlighters.addEnvMark
import com.tecknobit.envui.ide.highlighters.removeEnvMark
import com.tecknobit.envui.ide.languages.dEnvFileBase
import com.tecknobit.envui.ide.services.EnvSourcePreferencesManager
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

class dEnvFile(
    viewProvider: FileViewProvider,
) : dEnvFileBase(
    viewProvider,
    dEnvLanguage
) {

    override fun getFileType() = dEnvFileType

    fun updateValueForKey(
        key: String,
        value: String
    ) {
        val property = findPropertyByKey(
            key = key
        )!!

        upsertValue(
            property = property,
            value = value
        )
    }

    fun toggleMarkAsCritical(
        key: String,
        envSource: EnvSource
    ) {
        toggleEnvPref(
            key = key,
            envSource = envSource,
            preferenceType = CRITICAL,
            onPersistPref = { property, highlighter ->
                setPropertyCriticality(
                    source = envSource.source,
                    property = property,
                    isCritical = highlighter == null
                )
            }
        )
    }

    fun toggleResetOnClose(
        key: String,
        envSource: EnvSource
    ) {
        toggleEnvPref(
            key = key,
            envSource = envSource,
            preferenceType = RESET_ON_CLOSE,
            onPersistPref = { property, highlighter ->
                setPropertyResetOnClose(
                    source = envSource.source,
                    property = property,
                    resetOnClose = highlighter == null
                )
            }
        )
    }

    private inline fun toggleEnvPref(
        key: String,
        envSource: EnvSource,
        preferenceType: EnvSourcePreferenceType,
        crossinline onPersistPref: EnvSourcePreferencesManager.(Property, RangeHighlighter?) -> Unit
    ) {
        val property = findPropertyByKey(
            key = key
        )!!

        workWithDocument { document ->
            val highlighter = EnvSourceHighlightedPropertiesRegistry.getPropertyHighlighter(
                envSource = envSource,
                key = key,
                type = preferenceType
            )

            envSource.useEnvSourcePreferencesManager {
                if(highlighter != null) {
                    removeEnvMark(
                        highlighter = highlighter
                    )

                    EnvSourceHighlightedPropertiesRegistry.unmarkPropertyAsPrefType(
                        envSource = envSource,
                        key = key,
                        type = preferenceType
                    )
                } else {
                    resolveConflictualPreferences(
                        key = key,
                        envSource = envSource,
                        newPreference = preferenceType
                    )

                    val rangeHighlighter = addEnvMark(
                        document = document,
                        project = project,
                        line = document.getLineNumber(property.textRange.startOffset),
                        preferencesType = preferenceType
                    )

                    EnvSourceHighlightedPropertiesRegistry.markPropertyAsPrefType(
                        envSource = envSource,
                        key = key,
                        type = preferenceType,
                        highlighter = rangeHighlighter
                    )
                }

                onPersistPref(property, highlighter)
            }
        }
    }

    private fun resolveConflictualPreferences(
        key: String,
        envSource: EnvSource,
        newPreference: EnvSourcePreferenceType
    ) {
        val conflictualPreferences = getConflictualPreferences(
            newPreference = newPreference,
            envSource = envSource,
            key = key
        )

        conflictualPreferences.forEach { conflictualPreference ->
            toggleEnvPref(
                key = key,
                envSource = envSource,
                preferenceType = conflictualPreference,
                onPersistPref = { property, _ ->
                    setPropertyPreference(
                        source = envSource.source,
                        property = property,
                        onSet = { propertyPreferences ->
                            propertyPreferences.copy(
                                requireResetOnClose = false
                            )
                        }
                    )
                }
            )
        }
    }

    private fun getConflictualPreferences(
        newPreference: EnvSourcePreferenceType,
        envSource: EnvSource,
        key: String
    ): List<EnvSourcePreferenceType> {
        val activePreferences = getCurrentActivePreferences(
            envSource = envSource,
            key = key
        )
        val newPreferenceConflictualPreferences = newPreference.conflictualPreferences
        val conflictualPreferences = mutableListOf<EnvSourcePreferenceType>()

        newPreferenceConflictualPreferences.forEach { conflictualPreference ->
            if(activePreferences.contains(conflictualPreference))
                conflictualPreferences.add(conflictualPreference)
        }

        return conflictualPreferences
    }

    private fun getCurrentActivePreferences(
        envSource: EnvSource,
        key: String
    ): HashSet<EnvSourcePreferenceType> {
        val storedActivePreferences = project.useEnvSourcePreferencesManager {
            retrievePropertyPreferences(
                source = envSource.source,
                key = key
            )
        }

        val activePreferences = hashSetOf<EnvSourcePreferenceType>()
        if(storedActivePreferences.isCritical)
            activePreferences.add(CRITICAL)
        if(storedActivePreferences.requireResetOnClose)
            activePreferences.add(RESET_ON_CLOSE)

        return activePreferences
    }

    private fun dEnvFile.upsertValue(
        property: Property,
        value: String
    ) {
        val currentValueEntry = property.valueEntry
        val currentValue = currentValueEntry?.text ?: ""
        if(currentValue == value)
            return

        commitOnDocument { document ->
            if(currentValueEntry == null) {
                val equalsNode = property.node.findChildByType(
                    EnvGeneratedTypes.EQUALS
                )

                equalsNode?.let {
                    document.insertString(
                        equalsNode.textRange!!.endOffset,
                        value
                    )
                }
            } else {
                val currentValueTextRange = currentValueEntry.textRange

                document.replaceString(
                    currentValueTextRange.startOffset,
                    currentValueTextRange.endOffset,
                    value
                )
            }
        }
    }

}
