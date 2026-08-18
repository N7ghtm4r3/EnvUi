package com.tecknobit.envui.ide.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.utils.isNotEnvFile
import com.tecknobit.envui.utils.toEnvSource

fun AnActionEvent.resolveEnvSource(): EnvSource? {
    val editorFile = resolveVirtualFile() ?: return null
    val project = project ?: return null
    return editorFile.toEnvSource(
        project = project
    )
}

fun AnActionEvent.resolveVirtualFile(): VirtualFile? {
    val editorFile = getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
    if (editorFile.isNotEnvFile())
        return null

    return editorFile
}