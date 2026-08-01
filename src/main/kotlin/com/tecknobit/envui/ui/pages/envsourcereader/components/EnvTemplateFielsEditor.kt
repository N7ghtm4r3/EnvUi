package com.tecknobit.envui.ui.pages.envsourcereader.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.add
import com.tecknobit.envui.generated.resources.delete_template_field
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.ui.theme.EnvUiTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@Composable
fun EnvTemplateFieldsEditor(
    modifier: Modifier = Modifier,
    envSourceTemplate: EnvSourceTemplate,
    onSave: (EnvSourceTemplate) -> Unit,
) {
    val fields = remember {
        mutableStateListOf<EnvTemplateField>().apply {
            addAll(envSourceTemplate.fields)
        }
    }

    LazyColumn(
        modifier = modifier
            .animateContentSize(),
        contentPadding = PaddingValues(
            vertical = 16.dp,
            horizontal = 2.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stickyHeader {
            AddEntryButton(
                fields = fields
            )
        }

        itemsIndexed(
            items = fields,
            key = { index, field -> field.key + index }
        ) { index, field ->
            TemplateFieldEntry(
                modifier = Modifier
                    .animateItem(),
                field = field,
                onDelete = {
                    fields.removeAt(index)
                }
            )
        }
    }
}

@Composable
private fun AddEntryButton(
    fields: SnapshotStateList<EnvTemplateField>
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        DefaultButton(
            onClick = {
                fields.add(EnvTemplateField())
            }
        ) {
            Text(
                text = stringResource(Res.string.add)
            )
        }
    }
}

@Composable
private fun TemplateFieldEntry(
    modifier: Modifier = Modifier,
    field: EnvTemplateField,
    onDelete: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column (
            modifier = Modifier
                .weight(4f)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = TextFieldValue(field.key),
                onValueChange = {

                }
            )
        }

        Column (
            modifier = Modifier
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Dropdown(
                modifier = Modifier
                    .fillMaxWidth(),
                menuContent = {  },
                content = {
                    Text(
                        text = "fwfw"
                    )
                }
            )
        }

        Column (
            modifier = Modifier
                .weight(0.3f),
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    key = AllIconsKeys.General.Delete,
                    contentDescription = stringResource(Res.string.delete_template_field),
                    tint = EnvUiTheme.error
                )
            }
        }
    }
}
