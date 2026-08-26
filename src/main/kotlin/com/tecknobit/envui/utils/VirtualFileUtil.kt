package com.tecknobit.envui.utils

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFileType
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFileType

/**
 * Method used to resolve this path as a virtual file
 *
 * @receiver The path to resolve
 *
 * @return the resolved virtual file, if available, as [VirtualFile]
 */
fun String.toVirtualFile(): VirtualFile? {
    val path = kotlin.io.path.Path(this)

    return runReadAction {
        VfsUtil.findFile(path, true)
    }
}

/**
 * Method used to check whether this virtual file is not an environment source or template
 *
 * @return whether this is not an environment source file as [Boolean]
 */
fun VirtualFile?.isNotEnvSourceFile(): Boolean {
    return !isEnvSourceFile()
}

/**
 * Method used to check whether this virtual file is an environment source or template
 *
 * @return whether this is an environment source file as [Boolean]
 */
fun VirtualFile?.isEnvSourceFile(): Boolean {
    return isEnvFile() || isEnvTemplateFile()
}

/**
 * Method used to check whether this virtual file is not an environment file
 *
 * @return whether this is not an environment file as [Boolean]
 */
fun VirtualFile?.isNotEnvFile(): Boolean {
    return !isEnvFile()
}

/**
 * Method used to check whether this virtual file is an environment file
 *
 * @return whether this is an environment file as [Boolean]
 */
fun VirtualFile?.isEnvFile(): Boolean {
    return this != null && fileType == dEnvFileType
}

/**
 * Method used to check whether this virtual file is not an environment template file
 *
 * @return whether this is not an environment template file as [Boolean]
 */
fun VirtualFile?.isNotEnvTemplateFile(): Boolean {
    return !isEnvTemplateFile()
}

/**
 * Method used to check whether this virtual file is an environment template file
 *
 * @return whether this is an environment template file as [Boolean]
 */
fun VirtualFile?.isEnvTemplateFile(): Boolean {
    return this != null && fileType == dEnvTemplateFileType
}

/**
 * Method used to resolve the project containing this virtual file
 *
 * @return the containing project, if available, as [Project]
 */
fun VirtualFile?.resolveProject(): Project? {
    if (this == null)
        return null

    val projectLocator = ProjectLocator.getInstance()
    return projectLocator.guessProjectForFile(this)
}