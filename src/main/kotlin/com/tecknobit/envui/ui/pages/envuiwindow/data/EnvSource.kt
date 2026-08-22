package com.tecknobit.envui.ui.pages.envuiwindow.data

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.tecknobit.envui.ide.languages.envfile.dEnvFile
import com.tecknobit.envui.ide.languages.envfiletemplate.dEnvTemplateFile

/**
 * The `EnvSource` class is useful to represent an environment source and its optional template in a project
 *
 * @property project The project containing the environment source
 * @property source The virtual environment source file
 * @property module The optional module containing the source
 * @property _psiSource The `PSI` representation of the environment source
 * @property _templateSource The optional `PSI` representation of the associated template
 * @property isResolvedFromTemplate Whether the model was resolved starting from a template file
 *
 * @author N7ghtm4r3 - Tecknobit
 */
data class EnvSource(
    val project: Project,
    val source: VirtualFile,
    val module: Module?,
    private val _psiSource: PsiFile,
    private val _templateSource: PsiFile? = null,
    val isResolvedFromTemplate: Boolean,
) {

    /**
     * `name` the name of the environment source file
     */
    val name = source.name

    /**
     * `path` the path of the environment source file
     */
    val path = source.path

    /**
     * `containerFolder` the optional folder containing the environment source
     */
    val containerFolder: VirtualFile? = source.parent

    /**
     * `psiEnvSource` the typed `PSI` representation of the environment source
     */
    val psiEnvSource = _psiSource as dEnvFile

    /**
     * `psiEnvTemplateSource` the optional typed `PSI` representation of the associated template
     */
    var psiEnvTemplateSource = _templateSource as dEnvTemplateFile?

}
