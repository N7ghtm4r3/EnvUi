package com.tecknobit.envui.ide.languages.envfiletemplate

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.ide.theme.EnvUiIcons
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

object dEnvTemplateFileType : LanguageFileType(dEnvTemplateLanguage) {

    override fun getName(): @NonNls String {
        return "dEnvTemplate"
    }

    override fun getDescription(): @NlsContexts.Label String {
        return I18nMessageBundle.message(
            key = "env.template.file.description"
        )
    }

    override fun getDefaultExtension(): @NlsSafe String {
        return "template"
    }

    override fun getIcon(): Icon {
        return EnvUiIcons.EnvSourceTemplate
    }

}
