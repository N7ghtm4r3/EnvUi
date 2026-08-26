package com.tecknobit.envui.ide.languages.syntax

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.tecknobit.envui.ide.envfile.EnvGeneratedTypes
import com.tecknobit.envui.ide.languages.envfile.dEnvLexerAdapter

/**
 * The `dEnvSyntaxHighlighter` class is useful to assign editor text attributes to environment source tokens
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class dEnvSyntaxHighlighter : SyntaxHighlighterBase() {

    /**
     * Method used to retrieve the lexer used for environment syntax highlighting
     *
     * @return the environment lexer as [Lexer]
     */
    override fun getHighlightingLexer(): Lexer {
        return dEnvLexerAdapter()
    }

    /**
     * Method used to resolve the text attributes associated with an environment token
     *
     * @param token The token type to highlight
     *
     * @return the text attribute keys associated with the token as [Array] of [TextAttributesKey]
     */
    override fun getTokenHighlights(
        token: IElementType
    ): Array<out TextAttributesKey?> {
        return pack(
            when(token) {
                EnvGeneratedTypes.KEY -> DefaultLanguageHighlighterColors.KEYWORD
                EnvGeneratedTypes.VALUE -> DefaultLanguageHighlighterColors.STRING
                EnvGeneratedTypes.QUOTED_VALUE -> DefaultLanguageHighlighterColors.STRING
                EnvGeneratedTypes.COMMENT -> DefaultLanguageHighlighterColors.LINE_COMMENT
                EnvGeneratedTypes.EQUALS -> DefaultLanguageHighlighterColors.OPERATION_SIGN
                EnvGeneratedTypes.EXPORT -> DefaultLanguageHighlighterColors.STATIC_METHOD
                TokenType.BAD_CHARACTER -> HighlighterColors.BAD_CHARACTER

                else -> null
            }
        )
    }
}