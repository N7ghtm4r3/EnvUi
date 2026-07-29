package com.tecknobit.envui.com.tecknobit.envui.ui.envuiwindow.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.envui.com.tecknobit.envui.ui.envuiwindow.data.EnvSource
import org.jetbrains.jewel.ui.component.Text

@Composable
fun EnvSourcesList(
    modifier: Modifier = Modifier,
    sources: List<EnvSource>,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(
                all = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = sources,
            key = { envSource -> envSource.path }
        ) { envSource ->
            Text(
                text = envSource.name
            )
        }
    }
}