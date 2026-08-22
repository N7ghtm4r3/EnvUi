package com.tecknobit.envui.ide.listeners.document

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.isNotEnvFile
import com.tecknobit.envui.utils.resolveProject

/**
 * The `EnvSourceDocumentListener` class is useful to synchronize preferences before an environment source is saved
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class EnvSourceDocumentListener : FileDocumentManagerListener, dEnvDocumentListener {

    /**
     * Method used to synchronize environment source preferences before saving a document
     *
     * @param document The document being saved
     */
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

    /**
     * Method used to execute an operation when a document resolves to an environment source
     *
     * @param document The document to resolve
     * @param block The operation to execute with the source and its project
     */
    private inline fun requireEnvSource(
        document: Document,
        crossinline block: (EnvSource, Project) -> Unit,
    ) {
        val source = resolveSource(
            document = document
        )
        if (source.isNotEnvFile())
            return

        val project = source.resolveProject() ?: return
        val envSource = resolveEnvSource(source, project) ?: return

        block(envSource, project)
    }

}