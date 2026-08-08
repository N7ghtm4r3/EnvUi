package com.tecknobit.envui.ui.pages.envuiwindow.data

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile

data class EnvSource(
    val project: Project,
    val source: VirtualFile,
    val module: Module?,
    private val _psiSource: PsiFile,
    private val _templateSource: PsiFile? = null,
) {

    val name = source.name

    val path = source.path

    val containerFolder: VirtualFile? = source.parent

    val psiEnvSource = _psiSource as dEnvFile

    var psiEnvTemplateSource = _templateSource as dEnvTemplateFile?

}
