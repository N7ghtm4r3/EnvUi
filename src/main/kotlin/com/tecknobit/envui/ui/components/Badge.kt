package com.tecknobit.envui.ui.components

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.Text

val DefaultBadgeShape = RoundedCornerShape(
    size = 6.dp
)

@Composable
fun Badge(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    text: String,
    textSize: TextUnit = 14.sp,
    shape: Shape = DefaultBadgeShape,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = textSize
    ),
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
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
