package com.tecknobit.envui.ide.envfiletemplate

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.PsiTreeUtil
import com.tecknobit.envui.ide.envfile.Property
import org.jetbrains.annotations.Unmodifiable

class dEnvTemplateFile(
    viewProvider: FileViewProvider,
) : PsiFileBase(
    viewProvider,
    dEnvTemplateLanguage
) {

    companion object {

        const val ENV_TEMPLATE_FILENAME = ".env.template"

    }

    override fun getFileType() = dEnvTemplateFileType

    fun properties(): @Unmodifiable Collection<Property> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            Property::class.java
        )
    }

}