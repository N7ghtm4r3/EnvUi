package com.tecknobit.envui.utils

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile

/**
 * Method used to open this virtual file in the editor of the specified project
 *
 * @param project The project where the file is opened
 */
fun VirtualFile.openInEditor(
    project: Project,
) {
    val fileEditorManager = FileEditorManager.getInstance(project)

    fileEditorManager.openFile(this)
}

/**
 * Method used to reveal the first content root of this module in the project view
 *
 * @param onReveal The optional callback to invoke when the content root is revealed
 * @param project The project containing the module
 */
fun Module.revealInProjectView(
    onReveal: (() -> Any)? = null,
    project: Project,
) {
    val root = ModuleRootManager.getInstance(this)
        .contentRoots
        .firstOrNull()

    root?.revealInProjectView(
        onReveal = onReveal,
        project = project
    )
}

/**
 * Method used to reveal this virtual file in the project view
 *
 * @param onReveal The optional callback to invoke when the file is revealed
 * @param project The project containing the file
 */
fun VirtualFile.revealInProjectView(
    onReveal: (() -> Any)? = null,
    project: Project,
) {
    val projectView = ProjectView.getInstance(project)
    val onRevealImpl = if (onReveal == null)
        null
    else {
        Runnable {
            onReveal()
        }
    }

    projectView.select(
        onRevealImpl,
        this,
        true
    )
}

/**
 * Method used to execute an operation with a temporary editor for this document
 *
 * @param project The project owning the document
 * @param usage The operation to execute with the temporary editor
 */
inline fun Document.useInVirtualEditor(
    project: Project,
    usage: (Editor) -> Unit
) {
    val editorFactory = EditorFactory.getInstance()
    val editor = editorFactory.createEditor(this, project)

    try {
        usage(editor)
    } finally {
        editorFactory.releaseEditor(editor)
    }
}