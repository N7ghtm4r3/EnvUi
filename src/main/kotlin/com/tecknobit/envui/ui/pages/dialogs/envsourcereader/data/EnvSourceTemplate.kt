package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data

data class EnvSourceTemplate(
    val fields: List<EnvTemplateField> = emptyList(),
    val removedFields: HashSet<String> = hashSetOf()
)
