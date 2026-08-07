package com.tecknobit.envui.ide.languages.envfile

import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.invokeLater
import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.languages.dEnvFileBase
import com.tecknobit.envui.util.upsertValue

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
        )
        if(property == null)
            throw IllegalArgumentException("No property associated with that key")

        invokeLater(
            modalityState = ModalityState.current()
        ) {
            upsertValue(
                property = property,
                value = value
            )
        }
    }

    private fun findPropertyByKey(
        key: String
    ): Property? {
        return properties().firstOrNull { property ->
            property.keyEntry.text == key
        }
    }

}
