package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource

@Composable
fun EnvSourcesList(
    modifier: Modifier = Modifier,
    sources: List<EnvSource>,
) {
    LazyColumn(
        modifier = modifier
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = sources,
            key = { envSource -> envSource.path }
        ) { envSource ->
            EnvSourceCard(
                modifier = modifier
                    .animateItem()
                    .fillMaxWidth(),
                envSource = envSource,
                onClick = {
                }
            )
        }
    }
}