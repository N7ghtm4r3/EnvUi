package com.tecknobit.envui.ide.envfile

import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.ide.dEnvFileBase

class dEnvFile(
    viewProvider: FileViewProvider,
) : dEnvFileBase(
    viewProvider,
    dEnvLanguage
) {

    override fun getFileType() = dEnvFileType

}