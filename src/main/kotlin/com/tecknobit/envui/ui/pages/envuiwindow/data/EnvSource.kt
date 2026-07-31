package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

data class EnvSource(
    val project: Project,
    val source: VirtualFile,
    val module: Module?,
) {

    val name = source.name

    val path = source.path

    val containerFolder = source.parent

}