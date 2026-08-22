package com.tecknobit.envui.ide.listeners.document

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.resolveProject
import com.tecknobit.envui.utils.toEnvSource

/**
 * The `dEnvDocumentListener` interface defines the contract to resolve environment sources from editor documents
 *
 * @author N7ghtm4r3 - Tecknobit
 */
interface dEnvDocumentListener {

    /**
     * Method used to resolve the virtual file backing a document
     *
     * @param document The document to resolve
     *
     * @return the backing virtual file, if available, as [VirtualFile]
     */
    fun resolveSource(
        document: Document,
    ): VirtualFile? {
        val fileDocumentManager = FileDocumentManager.getInstance()

        return fileDocumentManager.getFile(document)
    }

    /**
     * Method used to resolve a virtual file as an environment source
     *
     * @param source The optional virtual file to resolve
     * @param project The optional project containing the source
     *
     * @return the resolved environment source, if available, as [EnvSource]
     */
    fun resolveEnvSource(
        source: VirtualFile?,
        project: Project? = null,
    ): EnvSource? {
        val currentProject = project ?: (source.resolveProject() ?: return null)

        return source!!.toEnvSource(
            project = currentProject
        )
    }

}