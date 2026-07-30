package com.tecknobit.envui.com.tecknobit.envui.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
    text: String,
    shape: Shape = RoundedCornerShape(
        size = BadgeRadiusDefault
    ),
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color,
) {
    Badge(
        modifier = modifier,
        text = text,
        shape = shape,
        textStyle = textStyle.copy(
            fontSize = 16.sp
        ),
        color = color
    )
}

@Composable
fun BadgeLabel(
    modifier: Modifier = Modifier,
    text: String,
    shape: Shape = RoundedCornerShape(
        size = 6.dp
    ),
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color,
) {
    Badge(
        modifier = modifier,
        text = text,
        shape = shape,
        textStyle = textStyle.copy(
            fontSize = 12.sp
        ),
        color = color
    )
}

@Composable
fun Badge(
    modifier: Modifier = Modifier,
    text: String,
    shape: Shape = RoundedCornerShape(
        size = BadgeRadiusDefault
    ),
    textStyle: TextStyle = LocalTextStyle.current,
    color: Color,
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(
                    vertical = 2.dp,
                    horizontal = 4.dp
                ),
            text = text,
            color = color,
            style = textStyle
        )
    }
}