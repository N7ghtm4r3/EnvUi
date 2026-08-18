package com.tecknobit.envui.ide.listeners

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.tecknobit.envui.utils.isNotEnvSourceFile

class EnvSourceDocumentListener : FileDocumentManagerListener {

    override fun beforeDocumentSaving(
        document: Document,
    ) {
        val fileDocumentManager = FileDocumentManager.getInstance()
        val source = fileDocumentManager.getFile(document)
        if (source.isNotEnvSourceFile())
            return
    }

}