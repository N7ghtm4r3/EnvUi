package com.tecknobit.envui.ide.languages.envfile

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.ide.envfile.EnvGeneratedTypes
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.highlighters.addCriticalEnvMark
import com.tecknobit.envui.ide.highlighters.addResetOnCloseMark
import com.tecknobit.envui.ide.languages.dEnvFileBase

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
        isMarked: Boolean
    ) {
        toggleEnvPref(
            key = key,
            toggleAction = { property, document ->
                val fileEditor = FileEditorManager.getInstance(project)

                addCriticalEnvMark(
                    editor = fileEditor.selectedTextEditor!!,
                    line = document.getLineNumber(property.textRange.startOffset)
                )
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
