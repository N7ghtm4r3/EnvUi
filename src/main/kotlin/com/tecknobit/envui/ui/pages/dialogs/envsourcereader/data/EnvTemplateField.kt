package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data

import com.tecknobit.envui.enums.EnvFieldType

data class EnvTemplateField(
    val key: String = "",
    val type: EnvFieldType = EnvFieldType.ANY,
)

