package com.tecknobit.envui.ide.languages.envfile

import com.intellij.psi.tree.IFileElementType

/**
 * The `dEnvTypes` object allows to access the root element types of the environment source language
 *
 * @author N7ghtm4r3 - Tecknobit
 */
object dEnvTypes {

    /**
     * `FILE` the root element type of an environment source file
     */
    val FILE = IFileElementType(dEnvLanguage)

}