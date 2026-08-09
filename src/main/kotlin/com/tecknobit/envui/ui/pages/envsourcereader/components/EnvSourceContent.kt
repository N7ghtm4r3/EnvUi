package com.tecknobit.envui.ui.pages.envsourcereader.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intellij.ui.JBColor
import com.tecknobit.envui.generated.resources.*
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.services.EnvSourcePreferencesManager
import com.tecknobit.envui.ui.components.Chip
import com.tecknobit.envui.ui.components.DebouncedInput
import com.tecknobit.envui.ui.components.LazyListScaffold
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.ui.theme.CardShape
import com.tecknobit.envui.ui.theme.EnvUiTheme
import com.tecknobit.envui.ui.utils.toComposeColor
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EnvSourceContent(
    modifier: Modifier = Modifier,
    envSource: EnvSource,
    onEmptyAction: () -> Unit
) {
    val psiEnvSource = envSource.psiEnvSource
    val properties = psiEnvSource.properties().toList()

    LazyListScaffold(
        items = properties,
        onEmpty = {
            NoEnvEntryAvailable(
                modifier = Modifier
                    .fillMaxSize(),
                action = {
                    DefaultButton(
                        onClick = onEmptyAction
                    ) {
                        Text(
                            text = stringResource(Res.string.manage_template)
                        )
                    }
                }
            )
        },
    ) {
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
                items = properties,
                key = { property -> property.keyEntry.text }
            ) { property ->
                EnvSourceProperty(
                    modifier = Modifier
                        .animateItem(),
                    property = property,
                    envSource = envSource,
                    onPropertyChange = { key, value ->
                        envSource.psiEnvSource.updateValueForKey(
                            key = key,
                            value = value
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EnvSourceProperty(
    modifier: Modifier = Modifier,
    property: Property,
    envSource: EnvSource,
    onPropertyChange: (String, String) -> Unit
) {
    Column(
        modifier = modifier
            .clip(CardShape)
            .border(
                width = 2.dp,
                color = EnvUiTheme.border,
                shape = CardShape
            )
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column (
            modifier = Modifier
                .padding(
                    all = 12.dp
                )
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(3f)
                ) {
                    Text(
                        text = property.keyEntry.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Actions(
                        envSource = envSource,
                        property = property
                    )
                }
            }

            EnvSourceInput(
                property = property,
                onPropertyChange = onPropertyChange
            )
        }
    }
}

@Composable
private fun Actions(
    envSource: EnvSource,
    property: Property
) {
    val envSourcePreferencesManager = EnvSourcePreferencesManager()
    val propertyPreferences = envSourcePreferencesManager.retrievePropertyPreferences(
        source = envSource.source,
        property = property
    )

    if (propertyPreferences == null)
        return

    Row (
        modifier = Modifier
            .padding(
                end = 1.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Chip(
            icon = AllIconsKeys.General.InspectionsWarning,
            text = Res.string.mark_as_critical_to_change,
            color = EnvUiTheme.error,
            isClicked = propertyPreferences.isCritical,
            onClick = {
                envSource.psiEnvSource.toggleMarkAsCritical(
                    key = property.keyEntry.text,
                    envSource = envSource
                )
            }
        )

        Chip(
            icon = AllIconsKeys.General.Reset,
            text = Res.string.reset_value_on_close,
            color = JBColor.gray.toComposeColor(),
            isClicked = propertyPreferences.requireResetOnClose,
            onClick = {
                envSource.psiEnvSource.toggleResetOnClose(
                    key = property.keyEntry.text,
                    envSource = envSource
                )
            }
        )
    }
}

@Composable
private fun EnvSourceInput(
    property: Property,
    onPropertyChange: (String, String) -> Unit
) {
    val valueEntry = property.valueEntry
    val key = property.keyEntry.text

    DebouncedInput(
        modifier = Modifier
            .fillMaxWidth(),
        delay = 200.milliseconds,
        initialValue = valueEntry?.text ?: "",
        placeholder = Res.string.enter_env_value_placeholder,
        onDebounce = { onPropertyChange(key, it) }
    )
}
