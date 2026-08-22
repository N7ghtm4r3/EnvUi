package com.tecknobit.envui.ui.utils

import androidx.compose.ui.graphics.Color as ComposeColor

/**
 * Method used to convert this `AWT` color into a Compose color
 *
 * @return the converted color as [ComposeColor]
 */
fun java.awt.Color.toComposeColor(): ComposeColor {
    return ComposeColor(
        red = this.red,
        green = this.green,
        blue = this.blue,
        alpha = this.alpha
    )
}
