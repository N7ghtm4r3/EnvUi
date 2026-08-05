package com.tecknobit.envui.ui.pages.envsourcereader.data

data class EnvSourceTemplate(
    val fields: List<EnvTemplateField> = emptyList(),
    val removedFields: HashSet<String> = hashSetOf()
)
