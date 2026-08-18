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

fun VirtualFile.openInEditor(
    project: Project,
) {
    val fileEditorManager = FileEditorManager.getInstance(project)

    fileEditorManager.openFile(this)
}

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