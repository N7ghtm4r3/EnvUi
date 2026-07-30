package com.tecknobit.envui.com.tecknobit.envui.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.Text

val BadgeRadiusDefault = 10.dp

@Composable
fun BadgeTitle(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    text: String,
    shape: Shape = RoundedCornerShape(
        size = BadgeRadiusDefault
    ),
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color,
    onClick: (() -> Unit)? = null,
) {
    Badge(
        modifier = modifier,
        icon = icon,
        text = text,
        shape = shape,
        textStyle = textStyle.copy(
            fontSize = 16.sp
        ),
        color = color,
        onClick = onClick
    )
}

@Composable
fun BadgeLabel(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    text: String,
    shape: Shape = RoundedCornerShape(
        size = 6.dp
    ),
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color,
    onClick: (() -> Unit)? = null,
) {
    Badge(
        modifier = modifier,
        icon = icon,
        text = text,
        shape = shape,
        textStyle = textStyle.copy(
            fontSize = 12.sp
        ),
        color = color,
        onClick = onClick
    )
}

@Composable
fun Badge(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    text: String,
    shape: Shape = RoundedCornerShape(
        size = BadgeRadiusDefault
    ),
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .clip(
                shape = shape
            )
            .background(
                color = color.copy(
                    alpha = 0.3f
                )
            )
            .clickable(
                enabled = onClick != null,
                onClick = onClick ?: {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(
                    vertical = 2.dp,
                    horizontal = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            icon?.invoke()

            Text(
                text = text,
                color = color,
                style = textStyle
            )
        }
    }
}