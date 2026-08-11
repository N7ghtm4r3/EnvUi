package com.tecknobit.envui.ui.pages.dialogs.propertypreferences.criticalenvsources.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.critical_env_sources_changed
import com.tecknobit.envui.ide.services.EnvSourcePreferences
import com.tecknobit.envui.ui.components.Card
import com.tecknobit.envui.ui.components.EmptyState
import com.tecknobit.envui.ui.components.LazyListScaffold
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@Composable
fun CriticalEnvSourcesList(
    modifier: Modifier = Modifier,
    criticalEnvSources: List<EnvSourcePreferences>
) {
    LazyListScaffold(
        items = criticalEnvSources,
        onEmpty = {
            EmptyState(
                icon = AllIconsKeys.Empty,
                title = Res.string.critical_env_sources_changed
            )
        },
        content = {
            LazyColumn(
                modifier = modifier
                    .animateContentSize(),
                contentPadding = PaddingValues(
                    vertical = 16.dp,
                    horizontal = 2.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = criticalEnvSources,
                    key = { criticalEnvSource -> criticalEnvSource.sourcePath }
                ) { criticalEnvSource ->
                    CriticalEnvSource(
                        modifier = Modifier
                            .animateItem(),
                        criticalEnvSource = criticalEnvSource
                    )
                }
            }
        }
    )
}

@Composable
private fun CriticalEnvSource(
    modifier: Modifier = Modifier,
    criticalEnvSource: EnvSourcePreferences
) {
    Card(
        modifier = modifier
    ) {
        Text(
            text = "gewgw"
        )
    }
}