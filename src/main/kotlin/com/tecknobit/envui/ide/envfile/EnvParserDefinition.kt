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

class EnvParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?): Lexer {
        return EnvLexerAdapter()
    }

    override fun createParser(p0: Project?): PsiParser {
        return EnvParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return EnvTypes.FILE
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(
        node: ASTNode,
    ): PsiElement {
        return EnvPsiElement(
            node = node
        )
    }

    override fun createFile(
        fileViewProvider: FileViewProvider,
    ): PsiFile {
        return EnvFile(
            viewProvider = fileViewProvider
        )
    }

}
