package com.tecknobit.envui.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tecknobit.envui.ui.theme.EnvUiTheme
import org.jetbrains.jewel.foundation.GlobalColors
import org.jetbrains.jewel.foundation.LocalGlobalColors
import org.jetbrains.jewel.foundation.OutlineColors
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.DefaultButton

/**
 * Custom button used to display a destructive action with error colors
 *
 * @param onClick The callback invoked when the button is clicked
 * @param modifier The modifier to apply to the button
 * @param enabled Whether the button accepts interactions
 * @param content The content displayed by the button
 */
@Composable
fun DestructiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val globalColors = JewelTheme.globalColors
    val error = EnvUiTheme.error
    val destructiveGlobalColors = remember(globalColors, error) {
        GlobalColors(
            borders = globalColors.borders,
            outlines = OutlineColors(
                focused = error,
                focusedWarning = globalColors.outlines.focusedWarning,
                focusedError = globalColors.outlines.focusedError,
                warning = globalColors.outlines.warning,
                error = globalColors.outlines.error
            ),
            text = globalColors.text,
            panelBackground = globalColors.panelBackground,
            toolwindowBackground = globalColors.toolwindowBackground
        )
    }

    CompositionLocalProvider(
        LocalGlobalColors provides destructiveGlobalColors
    ) {
        DefaultButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            style = EnvUiTheme.destructiveButtonStyle,
            content = content
        )
    }
}
