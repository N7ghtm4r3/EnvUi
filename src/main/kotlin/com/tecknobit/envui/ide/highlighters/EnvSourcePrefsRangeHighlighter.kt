package com.tecknobit.envui.ide.highlighters

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.tecknobit.envui.helpers.EnvSourcePreferencesType
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.CRITICAL
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.RESET_ON_CLOSE
import com.tecknobit.envui.ide.highlighters.icons.CriticalEnvGutterIcon
import com.tecknobit.envui.ide.highlighters.icons.ResetOnCloseGutterIcon

fun addCriticalEnvMark(
    editor: Editor,
    line: Int
): RangeHighlighter {
    return addEnvMark(
        editor = editor,
        line = line,
        preferencesType = CRITICAL
    )
}

fun addResetOnCloseMark(
    editor: Editor,
    line: Int
): RangeHighlighter {
    return addEnvMark(
        editor = editor,
        line = line,
        preferencesType = RESET_ON_CLOSE
    )
}

fun addEnvMark(
    editor: Editor,
    line: Int,
    preferencesType: EnvSourcePreferencesType
): RangeHighlighter {
    val highlighter = editor.markupModel.addLineHighlighter(
        line,
        HighlighterLayer.ADDITIONAL_SYNTAX,
        null
    )
    highlighter.gutterIconRenderer = when(preferencesType) {
        CRITICAL -> CriticalEnvGutterIcon()
        RESET_ON_CLOSE -> ResetOnCloseGutterIcon()
    }

    return highlighter
}

fun removeEnvMark(
    editor: Editor,
    highlighter: RangeHighlighter
) {
    editor.markupModel.removeHighlighter(highlighter)
}