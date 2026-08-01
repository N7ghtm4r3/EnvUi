package com.tecknobit.envui.ui.theme

import androidx.compose.ui.graphics.Color
import com.tecknobit.envui.ui.utils.toComposeColor
import javax.swing.UIManager

object EnvUiTheme {

    val primary: Color
        get() = nativeColorFromUi("Component.focusColor")

    val border: Color
        get() = nativeColorFromUi("Component.borderColor")

    private fun nativeColorFromUi(
        colorKey: String
    ): Color {
        return UIManager
            .getColor(colorKey)
            .toComposeColor()
    }

}
