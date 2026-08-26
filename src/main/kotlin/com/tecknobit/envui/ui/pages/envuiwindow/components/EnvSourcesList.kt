package com.tecknobit.envui.ui.pages.envuiwindow.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.envui_no_sources_message
import com.tecknobit.envui.generated.resources.loading_sources
import com.tecknobit.envui.ui.components.EmptyState
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.CircularProgressIndicatorBig
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys

/**
 * Component used to display loading, empty, or populated environment source content
 *
 * @param modifier The modifier to apply to the source list
 * @param sources The environment sources to display, or `null` while loading
 */
@Composable
fun EnvSourcesList(
    modifier: Modifier = Modifier,
    sources: List<EnvSource>?,
) {
    if (sources == null) {
        LoadingSourcesIndicator(
            modifier = Modifier
                .fillMaxSize()
        )
    } else {
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
}

/**
 * Component used to display the environment source loading indicator
 *
 * @param modifier The modifier to apply to the indicator container
 */
@Composable
private fun LoadingSourcesIndicator(
    modifier: Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicatorBig()

        Text(
            modifier = Modifier
                .padding(
                    top = 15.dp
                ),
            text = stringResource(Res.string.loading_sources),
            fontSize = 12.sp
        )
    }
}

/**
 * Component used to display the empty state of the environment source list
 *
 * @param modifier The modifier to apply to the empty state
 */
@Composable
private fun NoSourcesAvailable(
    modifier: Modifier = Modifier
) {
    EmptyState(
        modifier = modifier,
        icon = AllIconsKeys.Actions.Minimap,
        title = Res.string.envui_no_sources_message
    )
}
