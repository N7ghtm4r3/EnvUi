package com.tecknobit.envui.ide.languages.envfiletemplate

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.ide.theme.EnvUiIcons
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

/**
 * The `dEnvTemplateFileType` object allows to describe environment template files to the JetBrains IDE platform
 *
 * @author N7ghtm4r3 - Tecknobit
 */
object dEnvTemplateFileType : LanguageFileType(dEnvTemplateLanguage) {

    /**
     * Method used to retrieve the name of the environment template file type
     *
     * @return the file type name as [String]
     */
    override fun getName(): @NonNls String {
        return "dEnvTemplate"
    }

    /**
     * Method used to retrieve the localized description of the environment template file type
     *
     * @return the localized file type description as [String]
     */
    override fun getDescription(): @NlsContexts.Label String {
        return I18nMessageBundle.message(
            key = "env.template.file.description"
        )
    }

    /**
     * Method used to retrieve the default extension of environment template files
     *
     * @return the default file extension as [String]
     */
    override fun getDefaultExtension(): @NlsSafe String {
        return "template"
    }

    /**
     * Method used to retrieve the icon representing environment template files
     *
     * @return the environment template icon as [Icon]
     */
    override fun getIcon(): Icon {
        return EnvUiIcons.EnvSourceTemplate
    }

}
