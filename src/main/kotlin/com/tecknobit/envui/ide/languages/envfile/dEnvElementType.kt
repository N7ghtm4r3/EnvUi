package com.tecknobit.envui.ide.languages.envfile

import com.intellij.psi.tree.IElementType

/**
 * The `dEnvElementType` class is useful to represent an element type of the environment source language
 *
 * @param debugName The name used to identify the element type during debugging
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class dEnvElementType(
    debugName: String,
) : IElementType(
    debugName,
    dEnvLanguage,
)
