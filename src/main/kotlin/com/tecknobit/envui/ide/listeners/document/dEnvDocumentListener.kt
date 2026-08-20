package com.tecknobit.envui.ide.listeners.document

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
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
        val currentProject = project ?: (resolveProject(source) ?: return null)

        return source!!.toEnvSource(
            project = currentProject
        )
    }

    fun resolveProject(
        source: VirtualFile?,
    ): Project? {
        val projectLocator = ProjectLocator.getInstance()

        return projectLocator.guessProjectForFile(source!!)
    }

}