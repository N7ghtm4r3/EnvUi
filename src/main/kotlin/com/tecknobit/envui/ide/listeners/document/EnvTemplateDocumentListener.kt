package com.tecknobit.envui.ide.listeners.document

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.tecknobit.envui.utils.isNotEnvTemplateFile

class EnvTemplateDocumentListener : FileDocumentManagerListener, dEnvDocumentListener {

    override fun beforeDocumentSaving(
        document: Document,
    ) {
        val source = resolveSource(
            document = document
        )
        if (source.isNotEnvTemplateFile())
            return
    }

}