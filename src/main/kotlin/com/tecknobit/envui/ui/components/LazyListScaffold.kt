package com.tecknobit.envui.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable

@Composable
fun <T> LazyListScaffold(
    items: List<T>,
    onEmpty: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AnimatedContent(
        targetState = items.isEmpty()
    ) { isEmpty ->
        if(isEmpty)
            onEmpty()
        else
            content()
    }
}