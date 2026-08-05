package com.tecknobit.envui.ide.languages.envfiletemplate

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

object dEnvTemplateFileType : LanguageFileType(dEnvTemplateLanguage) {

    override fun getName(): @NonNls String {
        return "dEnvTemplate"
    }

    override fun getDescription(): @NlsContexts.Label String {
        return "Template for environment variables across project"
    }

    override fun getDefaultExtension(): @NlsSafe String {
        return "template"
    }

    override fun getIcon(): Icon? {
        return null //TODO: TO SET
    }

}
