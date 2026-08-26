@file:OptIn(ExperimentalJewelApi::class)

package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intellij.ui.JBColor
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.generated.resources.*
import com.tecknobit.envui.ide.services.useEnvSourcePreferencesManager
import com.tecknobit.envui.ui.components.LazyListScaffold
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data.EnvTemplateField
import com.tecknobit.envui.ui.pages.envuiwindow.data.EnvSource
import com.tecknobit.envui.ui.theme.EnvUiTheme
import com.tecknobit.envui.ui.utils.toComposeColor
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import kotlin.random.Random
import org.jetbrains.jewel.ui.component.items as menuItems

/**
 * The `EnvTemplateEditorField` class is useful to hold the editable state of an environment template field
 *
 * @property id The stable identifier of the editor field
 * @property _key The initial key of the field
 * @property _fieldType The initial value type of the field
 * @property isFilled Whether the field was loaded from the current template
 * @property changed The state indicating whether the field key changed
 *
 * @author N7ghtm4r3 - Tecknobit
 */
private data class EnvTemplateEditorField(
    val id: Long = Random.nextLong(),
    private val _key: String = "",
    private val _fieldType: EnvFieldType = EnvFieldType.ANY,
    val isFilled: Boolean = false,
    val changed: MutableState<Boolean> = mutableStateOf(false)
) {

    /**
     * `key` the editable key of the template field
     */
    var key by mutableStateOf(_key)

    /**
     * `fieldType` the editable value type of the template field
     */
    var fieldType by mutableStateOf(_fieldType)

}

/**
 * Component used to edit and persist the fields of an environment source template
 *
 * @param modifier The modifier to apply to the field list
 * @param envSource The environment source associated with the template
 * @param envSourceTemplate The current template displayed by the editor
 * @param onSave The callback invoked with the template after it is saved or becomes empty
 */
@Composable
fun EnvTemplateFieldsEditor(
    modifier: Modifier = Modifier,
    envSource: EnvSource,
    envSourceTemplate: EnvSourceTemplate,
    onSave: (EnvSourceTemplate) -> Unit,
) {
    val fields = remember { mutableStateListOf<EnvTemplateEditorField>() }
    val removedFields = remember { hashSetOf<String>() }

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

    LazyListScaffold(
        items = fields,
        onEmpty = {
            NoEnvEntryAvailable(
                modifier = Modifier
                    .fillMaxSize(),
                action = {
                    DefaultButton(
                        onClick = { fields.add(EnvTemplateEditorField()) }
                    ) {
                        Text(
                            text = stringResource(Res.string.add)
                        )
                    }
                }
            )
        }
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
                        val newEnvSourceTemplate = fields.saveAsTemplate(
                            removedFields = removedFields
                        )

                        envSource.useEnvSourcePreferencesManager {
                            upsertFromTemplate(
                                source = envSource.source,
                                envSourceTemplate = newEnvSourceTemplate,
                                onPropertyTypeChange = { key, value ->
                                    envSource.psiEnvSource.updateValueForKey(
                                        key = key,
                                        value = value
                                    )
                                }
                            )
                        }

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
                        removedFields.add(field.key)

                        if (fields.isEmpty()) {
                            val newEnvSourceTemplate = fields.saveAsTemplate(
                                removedFields = removedFields
                            )

                            onSave(newEnvSourceTemplate)
                        }
                    }
                )
            }
        }
    }
}

/**
 * Method used to check whether a valid draft differs from its initial environment template
 *
 * @param initialTemplate The initial environment template
 * @param draftEditorFields The current editable template fields
 *
 * @return whether the valid draft differs from the initial template as [Boolean]
 */
private fun isTemplateChanged(
    initialTemplate: EnvSourceTemplate,
    draftEditorFields: List<EnvTemplateEditorField>,
): Boolean {
    val initialTemplateFields = initialTemplate.fields
    val draftFields = draftEditorFields.toEnvTemplateFields()
    val allKeysValid = draftEditorFields.firstOrNull { it.key.isBlank() } == null

    return (initialTemplateFields != draftFields) && allKeysValid
}

/**
 * Method used to convert this field draft into a deduplicated environment template
 *
 * @param removedFields The keys removed while editing the template
 *
 * @return the environment template created from the draft as [EnvSourceTemplate]
 */
private fun List<EnvTemplateEditorField>.saveAsTemplate(
    removedFields: HashSet<String>,
): EnvSourceTemplate {
    return EnvSourceTemplate(
        fields = this
            .removeDuplicates()
            .toEnvTemplateFields(),
        removedFields = removedFields
    )
}

/**
 * Method used to convert this list of editor fields into environment template fields
 *
 * @return the converted fields as [List] of [EnvTemplateField]
 */
private fun List<EnvTemplateEditorField>.toEnvTemplateFields(): List<EnvTemplateField> {
    return map {
        EnvTemplateField(
            key = it.key,
            type = it.fieldType
        )
    }
}

/**
 * Method used to remove fields with duplicate keys from this editor field list
 *
 * @return the first editor field for each distinct key as [List] of [EnvTemplateEditorField]
 */
private fun List<EnvTemplateEditorField>.removeDuplicates(): List<EnvTemplateEditorField> {
    val sanitizedFields = mutableListOf<EnvTemplateEditorField>()
    forEach { templateEditorField ->
        val containsField = sanitizedFields.firstOrNull { templateEditorField.key == it.key } != null
        if(!containsField)
            sanitizedFields.add(templateEditorField)
    }

    return sanitizedFields
}

/**
 * Section used to display the template add and save actions
 *
 * @param onAdd The callback invoked when a field must be added
 * @param onSaveEnabled Whether the save action is enabled
 * @param onSave The callback invoked when the template must be saved
 */
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

/**
 * Component used to edit one template field and request its deletion
 *
 * @param modifier The modifier to apply to the field entry
 * @param field The editable template field displayed by the entry
 * @param onDelete The callback invoked when deletion is confirmed
 */
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
                    enabled = !field.changed.value,
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

/**
 * Component used to edit the key of a template field
 *
 * @param field The editable template field whose key is updated
 */
@Composable
private fun FieldKeyInput(
    field: EnvTemplateEditorField,
) {
    val initialFieldKeyValue = remember(field.id) { field.key }
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
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        onValueChange = {
            textFieldValue = it

            field.key = it.text
            field.changed.value = initialFieldKeyValue != it.text
        }
    )
}

/**
 * Component used to select the value type of a template field
 *
 * @param field The editable template field whose type is updated
 */
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

/**
 * Component used to request deletion of a template field
 *
 * @param enabled Whether the delete action is enabled
 * @param onDelete The callback invoked when deletion is requested
 */
@Composable
private fun DeleteFieldButton(
    enabled: Boolean,
    onDelete: () -> Unit,
) {
    IconButton(
        enabled = enabled,
        onClick = onDelete
    ) {
        Icon(
            key = AllIconsKeys.General.Delete,
            contentDescription = stringResource(Res.string.delete_template_field),
            tint = EnvUiTheme.error
        )
    }
}

/**
 * Component used to confirm or dismiss deletion of an existing template field
 *
 * @param show The state controlling banner visibility
 * @param onDelete The callback invoked when deletion is confirmed
 */
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
