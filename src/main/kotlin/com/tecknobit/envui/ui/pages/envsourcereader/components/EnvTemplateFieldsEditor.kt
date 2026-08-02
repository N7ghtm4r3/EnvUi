@file:OptIn(ExperimentalJewelApi::class)

package com.tecknobit.envui.ui.pages.envsourcereader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intellij.ui.JBColor
import com.tecknobit.envui.generated.resources.*
import com.tecknobit.envui.ui.enums.EnvFieldType
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.ui.theme.EnvUiTheme
import com.tecknobit.envui.ui.utils.toComposeColor
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import kotlin.random.Random
import org.jetbrains.jewel.ui.component.items as menuItems

private data class EnvTemplateEditorField(
    val id: Long = Random.nextLong(),
    private val _key: String = "",
    private val _fieldType: EnvFieldType = EnvFieldType.ANY,
    val isFilled: Boolean = false,
) {

    var key by mutableStateOf(_key)

    var fieldType by mutableStateOf(_fieldType)

}

@Composable
fun EnvTemplateFieldsEditor(
    modifier: Modifier = Modifier,
    envSourceTemplate: EnvSourceTemplate,
    onSave: (EnvSourceTemplate) -> Unit,
) {
    val fields = remember { mutableStateListOf<EnvTemplateEditorField>() }
    LaunchedEffect(envSourceTemplate) {
        fields.clear()

        envSourceTemplate.fields.forEach { field ->
            fields.add(
                EnvTemplateEditorField(
                    _key = field.key,
                    _fieldType = field.type,
                    isFilled = true
                )
            )
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
            Actions(
                onAdd = {
                    fields.add(EnvTemplateEditorField())
                },
                onSaveEnabled = isTemplateChanged(
                    initialTemplate = envSourceTemplate,
                    draftEditorFields = fields
                ),
                onSave = {
                    val newEnvSourceTemplate = EnvSourceTemplate(
                        fields = fields.toEnvTemplateFields()
                    )

                    onSave(newEnvSourceTemplate)
                }
            )
        }

        items(
            items = fields,
            key = { field -> field.id }
        ) { field ->
            TemplateFieldEntry(
                modifier = Modifier
                    .animateItem(),
                field = field,
                onDelete = {
                    fields.removeIf { field.id == it.id }
                }
            )
        }
    }
}

private fun isTemplateChanged(
    initialTemplate: EnvSourceTemplate,
    draftEditorFields: List<EnvTemplateEditorField>,
): Boolean {
    val initialTemplateFields = initialTemplate.fields
    val draftFields = draftEditorFields.toEnvTemplateFields()
    val allKeysValid = draftEditorFields.firstOrNull { it.key.isBlank() } == null

    return (initialTemplateFields != draftFields) && allKeysValid
}

private fun List<EnvTemplateEditorField>.toEnvTemplateFields(): List<EnvTemplateField> {
    return map {
        EnvTemplateField(
            key = it.key,
            type = it.fieldType
        )
    }
}

@Composable
private fun Actions(
    onAdd: () -> Unit,
    onSaveEnabled: Boolean,
    onSave: () -> Unit,
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(JBColor.background().toComposeColor()),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onAdd
            ) {
                Text(
                    text = stringResource(Res.string.add)
                )
            }

            DefaultButton(
                enabled = onSaveEnabled,
                onClick = onSave
            ) {
                Text(
                    text = stringResource(Res.string.save)
                )
            }
        }
    }
}

@Composable
private fun TemplateFieldEntry(
    modifier: Modifier = Modifier,
    field: EnvTemplateEditorField,
    onDelete: () -> Unit,
) {
    val deleteField = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(4f)
            ) {
                FieldKeyInput(
                    field = field
                )
            }

            Column(
                modifier = Modifier
                    .weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FieldTypeSelector(
                    field = field
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.3f),
                horizontalAlignment = Alignment.End
            ) {
                DeleteFieldButton(
                    onDelete = {
                        if (field.isFilled)
                            deleteField.value = true
                        else
                            onDelete()
                    }
                )
            }
        }

        DeleteFieldBannerAlert(
            show = deleteField,
            onDelete = onDelete
        )
    }
}

@Composable
private fun FieldKeyInput(
    field: EnvTemplateEditorField,
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = field.key
            )
        )
    }

    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it

            field.key = it.text
        }
    )
}

@Composable
private fun FieldTypeSelector(
    field: EnvTemplateEditorField,
) {
    var selectedFieldType by remember { mutableStateOf(field.fieldType) }

    Dropdown(
        modifier = Modifier
            .fillMaxWidth(),
        menuContent = {
            menuItems(
                items = EnvFieldType.entries,
                isSelected = { selectedFieldType == it },
                onItemClick = {
                    selectedFieldType = it

                    field.fieldType = it
                }
            ) { type ->
                Text(
                    text = type.displayName
                )
            }
        },
        content = {
            Text(
                text = selectedFieldType.displayName
            )
        }
    )
}

@Composable
private fun DeleteFieldButton(
    onDelete: () -> Unit,
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

@Composable
private fun DeleteFieldBannerAlert(
    show: MutableState<Boolean>,
    onDelete: () -> Unit,
) {
    AnimatedVisibility(
        visible = show.value
    ) {
        DefaultErrorBanner(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(2f)
                ) {
                    Text(
                        text = stringResource(Res.string.warning_deleting_template_field),
                        fontSize = 12.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.3f),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDelete
                        ) {
                            Icon(
                                key = AllIconsKeys.Actions.Checked,
                                contentDescription = stringResource(Res.string.confirm)
                            )
                        }

                        IconButton(
                            onClick = { show.value = false }
                        ) {
                            Icon(
                                key = AllIconsKeys.Actions.Close,
                                contentDescription = stringResource(Res.string.dismiss)
                            )
                        }
                    }
                }
            }
        }
    }
}