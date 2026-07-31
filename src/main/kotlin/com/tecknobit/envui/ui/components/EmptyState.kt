package com.tecknobit.envui.com.tecknobit.envui.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecknobit.envui.com.tecknobit.envui.ui.theme.EnvUiTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icon.IconKey

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    icon: IconKey,
    iconSize: Dp = 50.dp,
    title: StringResource,
    action: @Composable (() -> Unit)? = null,
) {
    val text = stringResource(title)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(iconSize),
                key = icon,
                tint = EnvUiTheme.primary,
                contentDescription = text
            )

            Text(
                text = text
            )

            action?.let { it() }
        }
    }
}