package com.tecknobit.envui.ide.languages.envfile

import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.ide.envfile.EnvGeneratedTypes
import com.tecknobit.envui.ide.envfile.Property
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

    fun handleCriticalityMark(
        key: String,
        isMarked: Boolean
    ) {
        addCommentOnProperty(
            key = key,
            comment = "⚠\uFE0F"
        )
    }

    fun handleResetOnCloseMark(
        key: String,
        isMarked: Boolean
    ) {
        addCommentOnProperty(
            key = key,
            comment = "\uD83D\uDD04"
        )
    }

    private fun dEnvFile.upsertValue(
        property: Property,
        value: String
    ) {
        val currentValueEntry = property.valueEntry
        val currentValue = currentValueEntry?.text ?: ""
        if(currentValue == value)
            return

        workOnDocument { document ->
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
