package com.tecknobit.envui.ide.languages.envfile

import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.ide.languages.dEnvFileBase

class dEnvFile(
    viewProvider: FileViewProvider,
) : dEnvFileBase(
    viewProvider,
    dEnvLanguage
) {

    override fun getFileType() = dEnvFileType

}