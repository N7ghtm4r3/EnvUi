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

/**
 * Method used to add a critical marker to an environment property line
 *
 * @param document The document containing the property
 * @param project The project owning the document
 * @param line The zero-based line to mark
 *
 * @return the range highlighter representing the marker as [RangeHighlighter]
 */
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

/**
 * Method used to add a reset-on-close marker to an environment property line
 *
 * @param document The document containing the property
 * @param project The project owning the document
 * @param line The zero-based line to mark
 *
 * @return the range highlighter representing the marker as [RangeHighlighter]
 */
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

/**
 * Method used to add or reuse a preference marker on an environment property line
 *
 * @param document The document containing the property
 * @param project The project owning the document
 * @param line The zero-based line to mark
 * @param preferencesType The preference represented by the marker
 *
 * @return the range highlighter representing the marker as [RangeHighlighter]
 */
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

/**
 * Method used to dispose an active environment property marker
 *
 * @param highlighter The range highlighter to dispose
 */
fun removeEnvMark(
    highlighter: RangeHighlighter
) {
    if(highlighter.isValid)
        highlighter.dispose()
}