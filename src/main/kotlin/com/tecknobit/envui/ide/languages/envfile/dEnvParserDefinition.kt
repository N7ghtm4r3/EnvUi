package com.tecknobit.envui.ide.languages.envfile

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.tecknobit.envui.ide.envfile.EnvGeneratedTypes
import com.tecknobit.envui.ide.envfile.EnvParser

/**
 * The `dEnvParserDefinition` class is useful to define parsing and `PSI` creation for environment source files
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class dEnvParserDefinition : ParserDefinition {

    /**
     * Method used to create the lexer for an environment source file
     *
     * @param project The optional project requesting the lexer
     *
     * @return the environment source lexer as [Lexer]
     */
    override fun createLexer(project: Project?): Lexer {
        return dEnvLexerAdapter()
    }

    /**
     * Method used to create the parser for an environment source file
     *
     * @param p0 The optional project requesting the parser
     *
     * @return the environment source parser as [PsiParser]
     */
    override fun createParser(p0: Project?): PsiParser {
        return EnvParser()
    }

    /**
     * Method used to retrieve the root node type of environment source files
     *
     * @return the root file node type as [IFileElementType]
     */
    override fun getFileNodeType(): IFileElementType {
        return dEnvTypes.FILE
    }

    /**
     * Method used to retrieve the token set representing environment comments
     *
     * @return the environment comment tokens as [TokenSet]
     */
    override fun getCommentTokens(): TokenSet {
        return TokenSet.create(EnvGeneratedTypes.COMMENT)
    }

    /**
     * Method used to retrieve the token set representing environment string values
     *
     * @return the environment string literal tokens as [TokenSet]
     */
    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.create(
            EnvGeneratedTypes.VALUE,
            EnvGeneratedTypes.QUOTED_VALUE,
        )
    }

    /**
     * Method used to create the `PSI` element represented by an abstract syntax tree node
     *
     * @param node The abstract syntax tree node to convert
     *
     * @return the generated `PSI` element as [PsiElement]
     */
    override fun createElement(
        node: ASTNode,
    ): PsiElement {
        return EnvGeneratedTypes.Factory.createElement(node)
    }

    /**
     * Method used to create an environment source `PSI` file
     *
     * @param fileViewProvider The view provider of the file
     *
     * @return the environment source `PSI` file as [PsiFile]
     */
    override fun createFile(
        fileViewProvider: FileViewProvider,
    ): PsiFile {
        return dEnvFile(
            viewProvider = fileViewProvider
        )
    }

}
