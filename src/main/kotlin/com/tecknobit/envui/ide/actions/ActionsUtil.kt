package com.tecknobit.envui.ide.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.util.toEnvSource

fun AnActionEvent.resolveEnvSource(): EnvSource? {
    val editorFile = resolveVirtualFile() ?: return null
    val project = project ?: return null
    return editorFile.toEnvSource(
        project = project
    )
}

fun AnActionEvent.resolveVirtualFile(): VirtualFile? {
    val editorFile = getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
    val fileType = editorFile.fileType
    if(fileType != dEnvFileType)
        return null

    return editorFile
}