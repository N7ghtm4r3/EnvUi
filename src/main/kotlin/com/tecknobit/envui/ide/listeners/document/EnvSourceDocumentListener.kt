package com.tecknobit.envui.ide.listeners.document

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.isNotEnvFile

class EnvSourceDocumentListener : FileDocumentManagerListener, dEnvDocumentListener {

    override fun beforeDocumentSaving(
        document: Document,
    ) {
        requireEnvSource(
            document = document
        ) { envSource, project ->
            project.useEnvSourcePreferencesManager {
                syncPreferencesFromSource(
                    envSource = envSource
                )
            }
        }
    }

    override fun afterDocumentSaved(
        document: Document,
    ) {
        requireEnvSource(
            document = document
        ) { envSource, _ ->
            syncTemplateFromSource(
                envSource = envSource
            )
        }
    }

    private inline fun requireEnvSource(
        document: Document,
        crossinline block: (EnvSource, Project) -> Unit,
    ) {
        val source = resolveSource(
            document = document
        )
        if (source.isNotEnvFile())
            return

        val project = resolveProject(source) ?: return
        val envSource = resolveEnvSource(source, project) ?: return

        block(envSource, project)
    }

}