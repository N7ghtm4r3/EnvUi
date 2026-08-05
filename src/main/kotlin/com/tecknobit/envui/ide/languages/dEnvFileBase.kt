package com.tecknobit.envui.ide.languages

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.Language
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.PsiTreeUtil
import com.tecknobit.envui.ide.envfile.KeyEntry
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.envfile.ValueEntry
import org.jetbrains.annotations.Unmodifiable

abstract class dEnvFileBase(
    viewProvider: FileViewProvider,
    language: Language
) : PsiFileBase(
    viewProvider,
    language
) {

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

    fun values(): @Unmodifiable Collection<ValueEntry> {
        return PsiTreeUtil.findChildrenOfType(
            this,
            ValueEntry::class.java
        )
    }

}
