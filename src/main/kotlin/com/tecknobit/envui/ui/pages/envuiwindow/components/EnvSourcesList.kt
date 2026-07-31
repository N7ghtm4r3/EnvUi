package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.envui.com.tecknobit.envui.ui.components.EmptyState
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.envui_no_sources_message
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@Composable
fun EnvSourcesList(
    modifier: Modifier = Modifier,
    sources: List<EnvSource>,
) {
    AnimatedContent(
        targetState = sources.isEmpty()
    ) { isEmpty ->
        if (isEmpty) {
            NoSourcesAvailable(
                modifier = Modifier
                    .fillMaxSize()
            )
        } else {
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
    }
}

@Composable
private fun NoSourcesAvailable(
    modifier: Modifier,
) {
    EmptyState(
        modifier = modifier,
        icon = AllIconsKeys.Actions.Minimap,
        iconSize = 75.dp,
        title = Res.string.envui_no_sources_message,
        action = {
            Text("Create new")
        }
    )
}