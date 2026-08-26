package com.tecknobit.envui.ide.languages.syntax

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * The `dEnvSyntaxHighlighterFactory` class is useful to create syntax highlighters for environment source files
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class dEnvSyntaxHighlighterFactory : SyntaxHighlighterFactory() {

    /**
     * Method used to create the syntax highlighter for an environment source file
     *
     * @param project The optional project requesting the highlighter
     * @param virtualFile The optional virtual file to highlight
     *
     * @return the environment syntax highlighter as [SyntaxHighlighter]
     */
    override fun getSyntaxHighlighter(
        project: Project?,
        virtualFile: VirtualFile?
    ): SyntaxHighlighter {
        return dEnvSyntaxHighlighter()
    }

}