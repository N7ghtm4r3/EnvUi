package com.tecknobit.envui.ide.languages.envfiletemplate

import com.intellij.psi.FileViewProvider
import com.tecknobit.envui.ide.languages.dEnvFileBase

/**
 * The `dEnvTemplateFile` class is useful to represent an environment template as a `PSI` file
 *
 * @param viewProvider The view provider of the `PSI` file
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class dEnvTemplateFile(
    viewProvider: FileViewProvider,
) : dEnvFileBase(
    viewProvider,
    dEnvTemplateLanguage
) {

    /**
     * The companion object allows to access the standard environment template filename
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    companion object {

        /**
         * `ENV_TEMPLATE_FILENAME` the standard environment template filename
         */
        const val ENV_TEMPLATE_FILENAME = ".env.template"

    }

    /**
     * Method used to retrieve the environment template file type
     *
     * @return the environment template file type as [dEnvTemplateFileType]
     */
    override fun getFileType() = dEnvTemplateFileType

}