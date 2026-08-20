package com.tecknobit.envui.ide.listeners.document

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.resolveProject
import com.tecknobit.envui.utils.toEnvSource

interface dEnvDocumentListener {

    fun resolveSource(
        document: Document,
    ): VirtualFile? {
        val fileDocumentManager = FileDocumentManager.getInstance()

        return fileDocumentManager.getFile(document)
    }

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