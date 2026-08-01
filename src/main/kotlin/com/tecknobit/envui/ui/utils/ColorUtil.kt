package com.tecknobit.envui.ui.utils

import androidx.compose.ui.graphics.Color as ComposeColor

fun java.awt.Color.toComposeColor(): ComposeColor {
    return ComposeColor(
        red = this.red,
        green = this.green,
        blue = this.blue,
        alpha = this.alpha
    )
}
