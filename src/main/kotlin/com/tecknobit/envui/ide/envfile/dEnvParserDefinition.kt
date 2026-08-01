package com.tecknobit.envui.ide.envfile

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

class dEnvParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?): Lexer {
        return dEnvLexerAdapter()
    }

    override fun createParser(p0: Project?): PsiParser {
        return EnvParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return dEnvTypes.FILE
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.create(EnvGeneratedTypes.COMMENT)
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.create(
            EnvGeneratedTypes.VALUE,
            EnvGeneratedTypes.QUOTED_VALUE,
        )
    }

    override fun createElement(
        node: ASTNode,
    ): PsiElement {
        return dEnvPsiElement(
            node = node
        )
    }

    override fun createFile(
        fileViewProvider: FileViewProvider,
    ): PsiFile {
        return dEnvFile(
            viewProvider = fileViewProvider
        )
    }

}
