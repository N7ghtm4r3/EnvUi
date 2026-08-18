package com.tecknobit.envui.ide.listeners.document

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.utils.isNotEnvSourceFile

class EnvSourceDocumentListener : FileDocumentManagerListener, dEnvDocumentListener {

    override fun beforeDocumentSaving(
        document: Document,
    ) {
        val source = resolveSource(
            document = document
        )
        if (source.isNotEnvSourceFile())
            return

        val project = resolveProject(source) ?: return
        val envSource = resolveEnvSource(
            source = source,
            project = project
        ) ?: return

        project.useEnvSourcePreferencesManager {
            syncPreferencesFromSource(
                envSource = envSource
            )
        }
    }

}