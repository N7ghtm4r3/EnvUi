package com.tecknobit.envui.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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