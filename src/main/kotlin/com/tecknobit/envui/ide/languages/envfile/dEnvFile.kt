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
import com.tecknobit.envui.ide.services.EnvSourcePropertyPreferences
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

/**
 * The `dEnvFile` class is useful to represent and edit an environment source as a `PSI` file
 *
 * @param viewProvider The view provider of the `PSI` file
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class dEnvFile(
    viewProvider: FileViewProvider,
) : dEnvFileBase(
    viewProvider,
    dEnvLanguage
) {

    /**
     * The companion object allows to access the standard environment source filename
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    companion object {

        /**
         * `ENV_FILENAME` the standard environment source filename
         */
        const val ENV_FILENAME = ".env"

    }

    /**
     * Method used to retrieve the environment source file type
     *
     * @return the environment source file type as [dEnvFileType]
     */
    override fun getFileType() = dEnvFileType

    /**
     * Method used to update and persist the value associated with an environment property key
     *
     * @param key The key of the property to update
     * @param value The value to assign to the property
     * @param synchronously Whether the document update must run synchronously
     */
    fun updateValueForKey(
        key: String,
        value: String,
        synchronously: Boolean = false
    ) {
        val property = findPropertyByKey(
            key = key
        ) ?: return

        upsertValue(
            property = property,
            value = value,
            synchronously = synchronously
        )

        project.useEnvSourcePreferencesManager {
            setPropertyValue(
                source = this@dEnvFile.virtualFile,
                property = property,
                value = value
            )
        }
    }

    /**
     * Method used to toggle the critical marker of an environment property
     *
     * @param key The key of the property
     * @param envSource The environment source containing the property
     */
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

    /**
     * Method used to toggle the reset-on-close marker of an environment property
     *
     * @param key The key of the property
     * @param envSource The environment source containing the property
     */
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

    /**
     * Method used to toggle a visual preference and persist its state for an environment property
     *
     * @param key The key of the property
     * @param envSource The environment source containing the property
     * @param preferenceType The preference type to toggle
     * @param onPersistPref The optional operation used to persist the toggled preference
     */
    private fun toggleEnvPref(
        key: String,
        envSource: EnvSource,
        preferenceType: EnvSourcePreferenceType,
        onPersistPref: (EnvSourcePreferencesManager.(Property, RangeHighlighter?) -> Unit)? = null
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

                onPersistPref?.invoke(this, property, highlighter)
            }
        }
    }

    /**
     * Method used to disable active preferences that conflict with a new preference
     *
     * @param key The key of the property
     * @param envSource The environment source containing the property
     * @param newPreference The preference that must become active
     */
    private fun resolveConflictualPreferences(
        key: String,
        envSource: EnvSource,
        newPreference: EnvSourcePreferenceType
    ) {
        val resolvedPropertyPreferences = EnvSourcePropertyPreferences(
            key = key
        )
        val newPreferenceConflictualPreferences = newPreference.conflictualPreferences

        val conflictualPreferences = getConflictualPreferences(
            newPreference = newPreference,
            envSource = envSource,
            key = key
        )
        conflictualPreferences.forEach { conflictualPreference ->
            toggleEnvPref(
                key = key,
                envSource = envSource,
                preferenceType = conflictualPreference
            )

            val flagValue = !newPreferenceConflictualPreferences.contains(conflictualPreference)
            when(newPreference) {
                CRITICAL -> {
                    resolvedPropertyPreferences.requireResetOnClose = flagValue
                }
                RESET_ON_CLOSE -> {
                    resolvedPropertyPreferences.isCritical = flagValue
                }
            }
        }

        envSource.useEnvSourcePreferencesManager {
            val property = findPropertyByKey(
                key = key
            )!!

            setPropertyPreference(
                source = envSource.source,
                property = property,
                onSet = { propertyPreferences ->
                    resolvedPropertyPreferences.copy(
                        initialValue = propertyPreferences.initialValue,
                        currentValue = propertyPreferences.currentValue,
                        lastUpdateAt = propertyPreferences.lastUpdateAt
                    )
                }
            )
        }
    }

    /**
     * Method used to retrieve the active preferences that conflict with a new preference
     *
     * @param newPreference The preference to compare with the active preferences
     * @param envSource The environment source containing the property
     * @param key The key of the property
     *
     * @return the active conflicting preferences as [List] of [EnvSourcePreferenceType]
     */
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

    /**
     * Method used to retrieve the active preferences of an environment property
     *
     * @param envSource The environment source containing the property
     * @param key The key of the property
     *
     * @return the active preferences as [HashSet] of [EnvSourcePreferenceType]
     */
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

    /**
     * Method used to insert or replace the value of an environment property in this file
     *
     * @param property The property whose value is updated
     * @param value The value to write
     * @param synchronously Whether the document update must run synchronously
     */
    private fun dEnvFile.upsertValue(
        property: Property,
        value: String,
        synchronously: Boolean
    ) {
        val currentValueEntry = property.valueEntry
        val currentValue = currentValueEntry?.text ?: ""
        if(currentValue == value)
            return

        commitOnDocument(
            synchronously = synchronously
        ) { document ->
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
