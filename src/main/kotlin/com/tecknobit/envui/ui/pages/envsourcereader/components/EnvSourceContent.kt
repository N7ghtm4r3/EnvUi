package com.tecknobit.envui.ui.pages.envsourcereader.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.manage_template
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.ide.envfile.ValueEntry
import com.tecknobit.envui.ui.components.LazyListScaffold
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.ui.theme.CardShape
import com.tecknobit.envui.ui.theme.EnvUiTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

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
                    property = property
                )
            }
        }
    }
}

@Composable
private fun EnvSourceProperty(
    modifier: Modifier = Modifier,
    property: Property
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
            Text(
                text = property.keyEntry.text,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            EnvSourceInput(
                valueEntry = property.valueEntry
            )
        }
    }
}

@Composable
private fun EnvSourceInput(
    valueEntry: ValueEntry?
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                valueEntry?.text ?: ""
            )
        )
    }

    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
        }
    )
}