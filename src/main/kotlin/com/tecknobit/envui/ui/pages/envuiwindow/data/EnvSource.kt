package com.tecknobit.envui.ui.pages.envuiwindow.data

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.tecknobit.envui.ide.envfile.dEnvFile

data class EnvSource(
    val project: Project,
    val source: VirtualFile,
    val module: Module?,
    private val psiSource: PsiFile,
) {

    val name = source.name

    val path = source.path

    val containerFolder = source.parent

    val psiEnvSource = psiSource as dEnvFile

}
