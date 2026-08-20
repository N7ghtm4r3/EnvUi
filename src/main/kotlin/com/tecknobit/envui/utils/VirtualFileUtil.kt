package com.tecknobit.envui.utils

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFileType

fun String.toVirtualFile(): VirtualFile? {
    val path = kotlin.io.path.Path(this)

    return runReadAction {
        VfsUtil.findFile(path, true)
    }
}

fun VirtualFile?.isNotEnvSourceFile(): Boolean {
    return !isEnvSourceFile()
}

fun VirtualFile?.isEnvSourceFile(): Boolean {
    return isEnvFile() || isEnvTemplateFile()
}

fun VirtualFile?.isNotEnvFile(): Boolean {
    return !isEnvFile()
}

fun VirtualFile?.isEnvFile(): Boolean {
    return this != null && fileType == dEnvFileType
}

fun VirtualFile?.isNotEnvTemplateFile(): Boolean {
    return !isEnvTemplateFile()
}

fun VirtualFile?.isEnvTemplateFile(): Boolean {
    return this != null && fileType == dEnvTemplateFileType
}

fun VirtualFile?.resolveProject(): Project? {
    if (this == null)
        return null

    val projectLocator = ProjectLocator.getInstance()
    return projectLocator.guessProjectForFile(this)
}