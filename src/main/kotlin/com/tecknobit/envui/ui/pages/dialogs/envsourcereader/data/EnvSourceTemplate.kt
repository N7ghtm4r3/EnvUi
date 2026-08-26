package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data

/**
 * The `EnvSourceTemplate` class is useful to represent the editable structure of an environment template
 *
 * @property fields The fields included in the template
 * @property removedFields The keys removed while editing the template
 *
 * @author N7ghtm4r3 - Tecknobit
 */
data class EnvSourceTemplate(
    val fields: List<EnvTemplateField> = emptyList(),
    val removedFields: HashSet<String> = hashSetOf()
)
