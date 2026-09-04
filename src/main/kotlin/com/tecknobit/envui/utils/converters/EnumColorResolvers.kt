package com.tecknobit.envui.utils.converters

import androidx.compose.ui.graphics.Color
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.enums.EnvFieldType.*
import com.tecknobit.envui.ui.utils.toComposeColor

/**
 * Method used to resolve the editor color associated with this environment field type
 *
 * @return the color associated with the field as [Color]
 */
fun EnvFieldType.toColor(): Color {
    val editorColorsManager = EditorColorsManager.getInstance()
    val scheme = editorColorsManager.globalScheme

    val colorKey = when (this) {
        STRING -> DefaultLanguageHighlighterColors.STRING
        INTEGER, LONG -> DefaultLanguageHighlighterColors.NUMBER
        FLOAT, DOUBLE -> DefaultLanguageHighlighterColors.CONSTANT
        BOOLEAN -> DefaultLanguageHighlighterColors.KEYWORD
        JSON -> DefaultLanguageHighlighterColors.METADATA
        ANY -> HighlighterColors.TEXT
    }

    val foregroundColor = scheme.getAttributes(colorKey)
        ?.foregroundColor
        ?: scheme.defaultForeground

    return foregroundColor.toComposeColor()
}