package com.tecknobit.envui.com.tecknobit.envui.ui.pages.envsourcereader.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.tecknobit.envui.com.tecknobit.envui.ui.pages.envsourcereader.data.EnvSourceTemplate
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun EnvTemplateFielsEditor(
    modifier: Modifier = Modifier,
    envSourceTemplate: EnvSourceTemplate,
    onSave: (EnvSourceTemplate) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        envSourceTemplate.fields.forEach { field ->
            TextField(
                value = TextFieldValue(field.key),
                onValueChange = {

                }
            )
        }
    }
}