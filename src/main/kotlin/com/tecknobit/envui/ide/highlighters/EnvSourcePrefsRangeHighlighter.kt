package com.tecknobit.envui.ide.highlighters

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.tecknobit.envui.ide.highlighters.icons.CriticalEnvGutterIcon
import com.tecknobit.envui.ide.highlighters.icons.ResetOnCloseGutterIcon

fun addCriticalEnvMark(
    editor: Editor,
    line: Int,
): RangeHighlighter {
    val highlighter = editor.markupModel.addLineHighlighter(
        line,
        HighlighterLayer.ADDITIONAL_SYNTAX,
        null
    )

    highlighter.gutterIconRenderer = CriticalEnvGutterIcon()

    return highlighter
}

fun addResetOnCloseMark(
    editor: Editor,
    line: Int,
): RangeHighlighter {
    val highlighter = editor.markupModel.addLineHighlighter(
        line,
        HighlighterLayer.ADDITIONAL_SYNTAX,
        null
    )

    highlighter.gutterIconRenderer = ResetOnCloseGutterIcon()

    return highlighter
}