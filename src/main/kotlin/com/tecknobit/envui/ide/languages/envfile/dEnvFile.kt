package com.tecknobit.envui.ide.languages.envfile

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.helpers.EnvSourceHighlightedPropertiesRegistry
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.CRITICAL
import com.tecknobit.envui.ide.envfile.EnvGeneratedTypes
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.highlighters.addCriticalEnvMark
import com.tecknobit.envui.ide.highlighters.addResetOnCloseMark
import com.tecknobit.envui.ide.highlighters.removeCriticalEnvMark
import com.tecknobit.envui.ide.languages.dEnvFileBase
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
            toggleAction = { property, document ->
                val fileEditor = FileEditorManager.getInstance(project)
                val editor = fileEditor.selectedTextEditor!!
                val highlighter = EnvSourceHighlightedPropertiesRegistry.getPropertyHighlighter(
                    envSource = envSource,
                    key = key,
                    type = CRITICAL
                )

                if(highlighter != null) {
                    removeCriticalEnvMark(
                        editor = editor,
                        highlighter = highlighter
                    )


                } else {
                    val rangeHighlighter = addCriticalEnvMark(
                        editor = editor,
                        line = document.getLineNumber(property.textRange.startOffset)
                    )

                    EnvSourceHighlightedPropertiesRegistry.markPropertyAsCritical(
                        envSource = envSource,
                        key = key,
                        highlighter = rangeHighlighter
                    )
                }
            }
        )
    }

    fun toggleResetOnClose(
        key: String,
        isMarked: Boolean
    ) {
        toggleEnvPref(
            key = key,
            toggleAction = { property, document ->
                val fileEditor = FileEditorManager.getInstance(project)

                addResetOnCloseMark(
                    editor = fileEditor.selectedTextEditor!!,
                    line = document.getLineNumber(property.textRange.startOffset)
                )
            }
        )
    }

    private inline fun toggleEnvPref(
        key: String,
        crossinline toggleAction: (Property, Document) -> Unit
    ) {
        val property = findPropertyByKey(
            key = key
        )!!

        workWithDocument { document ->
            toggleAction(property, document)
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
