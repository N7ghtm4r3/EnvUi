package com.tecknobit.envui.ide.languages.envfile

import com.intellij.lexer.FlexAdapter
import com.tecknobit.envui.ide.envfile._dEnvLexer

/**
 * The `dEnvLexerAdapter` class is useful to adapt the generated environment lexer to the JetBrains IDE lexer contract
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class dEnvLexerAdapter : FlexAdapter(_dEnvLexer())
