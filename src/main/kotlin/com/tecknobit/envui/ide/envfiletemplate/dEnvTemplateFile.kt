package com.tecknobit.envui.ide.envfiletemplate

import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.ide.dEnvFileBase

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