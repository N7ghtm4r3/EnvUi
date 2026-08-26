package com.tecknobit.envui.ide.languages.envfile

import com.intellij.psi.tree.IElementType

/**
 * The `dEnvTokenType` class is useful to represent a token type of the environment source language
 *
 * @param debugName The name used to identify the token type during debugging
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class dEnvTokenType(
    debugName: String,
) : IElementType(
    debugName,
    dEnvLanguage,
)
