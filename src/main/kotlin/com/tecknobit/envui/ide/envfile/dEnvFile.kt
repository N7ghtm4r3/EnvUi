package com.tecknobit.envui.ide.envfile

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.Unmodifiable

class dEnvFile(
    viewProvider: FileViewProvider,
) : PsiFileBase(
    viewProvider,
    dEnvLanguage
) {

    override fun getFileType() = dEnvFileType

    fun properties(): @Unmodifiable Collection<Property> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            Property::class.java
        )
    }

    fun keys(): @Unmodifiable Collection<KeyEntry> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            KeyEntry::class.java
        )
    }

}