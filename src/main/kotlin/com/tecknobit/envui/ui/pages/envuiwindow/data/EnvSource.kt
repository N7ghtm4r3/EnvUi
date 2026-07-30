package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data

import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

data class EnvSource(
    val project: Project,
    val source: VirtualFile,
) {

    val name = source.name

    val path = source.path

    val module
        get() = ModuleUtilCore.findModuleForFile(source, project)

    val containerFolder = source.parent

    val isModuleRootLocated = containerFolder == null

}