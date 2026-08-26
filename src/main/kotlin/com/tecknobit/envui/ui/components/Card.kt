package com.tecknobit.envui.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.tecknobit.envui.ui.theme.EnvUiTheme

/**
 * `CardShape` the default rounded shape applied to cards
 */
val CardShape = RoundedCornerShape(
    size = 12.dp
)

/**
 * Component used to display bordered column content with an optional click action
 *
 * @param modifier The modifier to apply to the card
 * @param shape The shape of the card
 * @param onClick The optional callback invoked when the card is clicked
 * @param content The column content displayed by the card
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(
                shape = shape,
            )
            .border(
                width = 2.dp,
                color = EnvUiTheme.border,
                shape = shape
            )
            .clickable(
                enabled = onClick != null,
                onClick = onClick ?: {}
            )
    ) {
        Column(
            modifier = Modifier
                .padding(
                    all = 12.dp
                ),
            content = content
        )
    }
}