package com.tecknobit.envui.ide.highlighters

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.tecknobit.envui.helpers.EnvSourcePreferencesType
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.CRITICAL
import com.tecknobit.envui.helpers.EnvSourcePreferencesType.RESET_ON_CLOSE
import com.tecknobit.envui.ide.highlighters.icons.CriticalEnvGutterIcon
import com.tecknobit.envui.ide.highlighters.icons.ResetOnCloseGutterIcon

fun addCriticalEnvMark(
    document: Document,
    project: Project,
    line: Int
): RangeHighlighter {
    return addEnvMark(
        document = document,
        project = project,
        line = line,
        preferencesType = CRITICAL
    )
}

fun addResetOnCloseMark(
    document: Document,
    project: Project,
    line: Int
): RangeHighlighter {
    return addEnvMark(
        document = document,
        project = project,
        line = line,
        preferencesType = RESET_ON_CLOSE
    )
}

fun addEnvMark(
    document: Document,
    project: Project,
    line: Int,
    preferencesType: EnvSourcePreferencesType
): RangeHighlighter {
    val markupModel = DocumentMarkupModel.forDocument(
        document,
        project,
        true
    )

    val highlighter = markupModel.addLineHighlighter(
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
    highlighter: RangeHighlighter
) {
    if(highlighter.isValid)
        highlighter.dispose()
}