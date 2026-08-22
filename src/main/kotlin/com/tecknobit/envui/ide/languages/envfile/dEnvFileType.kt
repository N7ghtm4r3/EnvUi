package com.tecknobit.envui.ide.languages.envfile

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import com.tecknobit.envui.I18nMessageBundle
import com.tecknobit.envui.ide.theme.EnvUiIcons
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

/**
 * The `dEnvFileType` object allows to describe environment source files to the JetBrains IDE platform
 *
 * @author N7ghtm4r3 - Tecknobit
 */
object dEnvFileType : LanguageFileType(dEnvLanguage) {

    /**
     * Method used to retrieve the name of the environment source file type
     *
     * @return the file type name as [String]
     */
    override fun getName(): @NonNls String {
        return "dEnv"
    }

    /**
     * Method used to retrieve the localized description of the environment source file type
     *
     * @return the localized file type description as [String]
     */
    override fun getDescription(): @NlsContexts.Label String {
        return I18nMessageBundle.message(
            key = "env.file.description"
        )
    }

    /**
     * Method used to retrieve the default extension of environment source files
     *
     * @return the default file extension as [String]
     */
    override fun getDefaultExtension(): @NlsSafe String {
        return "env"
    }

    /**
     * Method used to retrieve the icon representing environment source files
     *
     * @return the environment source icon as [Icon]
     */
    override fun getIcon(): Icon {
        return EnvUiIcons.EnvSource
    }

}
