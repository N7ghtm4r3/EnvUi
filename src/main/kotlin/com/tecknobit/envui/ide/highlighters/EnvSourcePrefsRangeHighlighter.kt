package com.tecknobit.envui.ide.highlighters

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.tecknobit.envui.helpers.EnvSourcePreferenceType
import com.tecknobit.envui.helpers.EnvSourcePreferenceType.CRITICAL
import com.tecknobit.envui.helpers.EnvSourcePreferenceType.RESET_ON_CLOSE
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
    preferencesType: EnvSourcePreferenceType
): RangeHighlighter {
    val markupModel = DocumentMarkupModel.forDocument(
        document,
        project,
        true
    )

    val gutterIconRenderer = when(preferencesType) {
        CRITICAL -> CriticalEnvGutterIcon()
        RESET_ON_CLOSE -> ResetOnCloseGutterIcon()
    }

    var highlighter = markupModel.allHighlighters.firstOrNull { highlighter ->
        val highlighterLine = document.getLineNumber(highlighter.startOffset)

        highlighterLine == line && highlighter.gutterIconRenderer?.icon == gutterIconRenderer.icon
    }

    if(highlighter == null) {
        highlighter = markupModel.addLineHighlighter(
            line,
            HighlighterLayer.ADDITIONAL_SYNTAX,
            null
        )
        highlighter.gutterIconRenderer = gutterIconRenderer
    }

    return highlighter
}

fun removeEnvMark(
    highlighter: RangeHighlighter
) {
    if(highlighter.isValid)
        highlighter.dispose()
}