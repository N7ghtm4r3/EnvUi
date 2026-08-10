package com.tecknobit.envui.ide.languages.envfile

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.helpers.EnvSourceHighlightedPropertiesRegistry
import com.tecknobit.envui.helpers.EnvSourcePreferencesType
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.CRITICAL
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.RESET_ON_CLOSE
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
            preferencesType = CRITICAL,
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
            preferencesType = RESET_ON_CLOSE,
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
        preferencesType: EnvSourcePreferencesType,
        crossinline onPersistPref: EnvSourcePreferencesManager.(Property, RangeHighlighter?) -> Unit
    ) {
        val property = findPropertyByKey(
            key = key
        )!!

        workWithDocument { document ->
            val highlighter = EnvSourceHighlightedPropertiesRegistry.getPropertyHighlighter(
                envSource = envSource,
                key = key,
                type = preferencesType
            )

            envSource.useEnvSourcePreferencesManager {
                if(highlighter != null) {
                    removeEnvMark(
                        highlighter = highlighter
                    )

                    EnvSourceHighlightedPropertiesRegistry.unmarkPropertyAsPrefType(
                        envSource = envSource,
                        key = key,
                        type = preferencesType
                    )
                } else {
                    val rangeHighlighter = addEnvMark(
                        document = document,
                        project = project,
                        line = document.getLineNumber(property.textRange.startOffset),
                        preferencesType = preferencesType
                    )

                    EnvSourceHighlightedPropertiesRegistry.markPropertyAsPrefType(
                        envSource = envSource,
                        key = key,
                        type = preferencesType,
                        highlighter = rangeHighlighter
                    )
                }

                onPersistPref(property, highlighter)
            }
        }
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
