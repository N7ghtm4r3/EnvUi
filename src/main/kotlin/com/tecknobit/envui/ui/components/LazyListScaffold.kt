package com.tecknobit.envui.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Component used to alternate between empty-state content and list content
 *
 * @param T The item type of the list
 * @param items The items used to determine whether the list is empty
 * @param onEmpty The content displayed when no items are available
 * @param content The content displayed when items are available
 */
@Composable
fun <T> LazyListScaffold(
    items: List<T>,
    onEmpty: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    AnimatedContent(
        targetState = items.isEmpty()
    ) { isEmpty ->
        if(isEmpty) {
            Column (
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                onEmpty()
            }
        } else
            content()
    }
}