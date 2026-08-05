package com.tecknobit.envui.ide.languages.envfiletemplate

import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.ide.languages.dEnvFileBase

class dEnvTemplateFile(
    viewProvider: FileViewProvider,
) : dEnvFileBase(
    viewProvider,
    dEnvTemplateLanguage
) {

    companion object {

        const val ENV_TEMPLATE_FILENAME = ".env.template"

    }

    override fun getFileType() = dEnvTemplateFileType

}