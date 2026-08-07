@file:OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package com.tecknobit.envui.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.envui.ui.theme.EnvUiTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.icon.IconKey

val DefaultChipShape = RoundedCornerShape(
    size = 6.dp
)

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    icon: IconKey,
    iconSize: Dp = 16.dp,
    text: StringResource,
    shape: Shape = DefaultChipShape,
    color: Color = EnvUiTheme.primary,
    width: Dp = 50.dp,
    onClick: (Boolean) -> Unit,
) {
    val hint = stringResource(text)
    var clicked by rememberSaveable { mutableStateOf(false) }
    val componentColor = remember(clicked) {
        color.copy(
            alpha = if(clicked)
                0.3f
            else
                0f
        )
    }

    Tooltip(
        tooltip = {
            Text(
                text = hint,
                fontSize = 12.sp
            )
        },
        content = {
            Box(
                modifier = modifier
                    .clip(
                        shape = shape
                    )
                    .border(
                        width = 1.dp,
                        color = color,
                        shape = shape
                    )
                    .background(
                        color = componentColor
                    )
                    .clickable {
                        clicked = !clicked
                        onClick(clicked)
                    }
                    .width(width)
                    .semantics(
                        properties = { Role.Button }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .padding(
                            all = 4.dp
                        )
                        .size(
                            size = iconSize
                        ),
                    key = icon,
                    contentDescription = hint,
                    tint = color
                )
            }
        }
    )
}
