package com.tecknobit.envui.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import com.tecknobit.envui.ui.utils.toComposeColor
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.styling.ButtonColors
import org.jetbrains.jewel.ui.component.styling.ButtonStyle
import org.jetbrains.jewel.ui.theme.defaultButtonStyle
import javax.swing.UIManager

object EnvUiTheme {

    val primary: Color
        get() = nativeColorFromUi("Component.focusColor")

    val background: Color
        get() = JBColor.background().toComposeColor()

    val surface: Color
        get() = nativeColorFromUi("EditorPane.background")

    val border: Color
        get() = nativeColorFromUi("Component.borderColor")

    val error: Color
        get() = nativeColorFromUi("Component.errorFocusColor")

    val mutedColor: Color
        get() = UIUtil
            .getLabelInfoForeground()
            .toComposeColor()

    private fun nativeColorFromUi(
        colorKey: String
    ): Color {
        return UIManager
            .getColor(colorKey)
            .toComposeColor()
    }

    val destructiveButtonStyle: ButtonStyle
        @Composable
        get() {
            val defaultStyle = JewelTheme.defaultButtonStyle

            return remember(defaultStyle, error) {
                val disabled = error.copy(alpha = 0.4f)
                val hovered = lerp(error, Color.White, 0.08f)
                val pressed = lerp(error, Color.Black, 0.08f)

                ButtonStyle(
                    colors = ButtonColors(
                        background = SolidColor(error),
                        backgroundDisabled = SolidColor(disabled),
                        backgroundFocused = SolidColor(error),
                        backgroundPressed = SolidColor(pressed),
                        backgroundHovered = SolidColor(hovered),
                        content = Color.White,
                        contentDisabled = Color.White.copy(alpha = 0.5f),
                        contentFocused = Color.White,
                        contentPressed = Color.White,
                        contentHovered = Color.White,
                        border = SolidColor(error),
                        borderDisabled = SolidColor(disabled),
                        borderFocused = SolidColor(error),
                        borderPressed = SolidColor(pressed),
                        borderHovered = SolidColor(hovered)
                    ),
                    metrics = defaultStyle.metrics,
                    focusOutlineAlignment = defaultStyle.focusOutlineAlignment
                )
            }
        }

}
