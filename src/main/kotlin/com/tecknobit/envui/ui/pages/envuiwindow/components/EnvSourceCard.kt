package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tecknobit.envui.com.tecknobit.envui.ui.components.Badge
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.com.tecknobit.envui.ui.utils.toComposeColor
import java.awt.Color

@Composable
fun EnvSourceCard(
    modifier: Modifier = Modifier,
    envSource: EnvSource,
) {
    Box(
        modifier = modifier
    ) {
        Column {
            Badge(
                text = envSource.name,
                color = Color.cyan.toComposeColor(),
            )
        }
    }
}