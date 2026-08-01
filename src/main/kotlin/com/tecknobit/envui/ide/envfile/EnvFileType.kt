package com.tecknobit.envui.ide.envfile

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

object EnvFileType : LanguageFileType(dEnvLanguage) {

    override fun getName(): @NonNls String {
        return "dEnv"
    }

    override fun getDescription(): @NlsContexts.Label String {
        return "Environment variables across project"
    }

    override fun getDefaultExtension(): @NlsSafe String {
        return ".env"
    }

    override fun getIcon(): Icon? {
        TODO("Not yet implemented")
    }

}