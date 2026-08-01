package com.tecknobit.envui.ui.pages.envsourcereader.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvSourceTemplate
import com.tecknobit.envui.ui.pages.envsourcereader.data.EnvTemplateField
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun EnvTemplateFielsEditor(
    modifier: Modifier = Modifier,
    envSourceTemplate: EnvSourceTemplate,
    onSave: (EnvSourceTemplate) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .animateContentSize()
    ) {
        items(
            items = envSourceTemplate.fields,
            key = { field -> field.key }
        ) { field ->
            TemplateFieldEntry(
                modifier = Modifier
                    .animateItem(),
                field = field
            )
        }
    }
}

@Composable
private fun TemplateFieldEntry(
    modifier: Modifier = Modifier,
    field: EnvTemplateField,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .animateContentSize(),
            value = TextFieldValue(field.key),
            onValueChange = {

            }
        )
    }
}
