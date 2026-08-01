package com.tecknobit.envui.ide.envfile

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class dEnvFile(
    viewProvider: FileViewProvider,
) : PsiFileBase(
    viewProvider,
    dEnvLanguage
) {

    override fun getFileType() = dEnvFileType

}