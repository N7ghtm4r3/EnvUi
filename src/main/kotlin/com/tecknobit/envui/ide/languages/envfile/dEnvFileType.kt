package com.tecknobit.envui.ide.languages.envfile

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.ide.theme.EnvUiIcons
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

object dEnvFileType : LanguageFileType(dEnvLanguage) {

    override fun getName(): @NonNls String {
        return "dEnv"
    }

    override fun getDescription(): @NlsContexts.Label String {
        return I18nMessageBundle.message(
            key = "env.file.description"
        )
    }

    override fun getDefaultExtension(): @NlsSafe String {
        return "env"
    }

    override fun getIcon(): Icon {
        return EnvUiIcons.EnvSource
    }

}
