package com.tecknobit.envui.com.tecknobit.envui.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Text

val BadgeRadiusDefault = 12.dp

@Composable
fun Badge(
    modifier: Modifier = Modifier,
    text: String,
    shape: Shape = RoundedCornerShape(
        size = BadgeRadiusDefault
    ),
    color: androidx.compose.ui.graphics.Color,
) {
    Box(
        modifier = modifier
            .clip(
                shape = shape
            )
            .background(
                color = color.copy(
                    alpha = 0.1f
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
            color = color
        )
    }
}