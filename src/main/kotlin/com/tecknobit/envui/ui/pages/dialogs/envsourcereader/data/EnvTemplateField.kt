package com.tecknobit.envui.ui.pages.dialogs.envsourcereader.data

import com.tecknobit.envui.enums.EnvFieldType

/**
 * The `EnvTemplateField` class is useful to represent a field declared by an environment template
 *
 * @property key The key of the template field
 * @property type The expected value type of the template field
 *
 * @author N7ghtm4r3 - Tecknobit
 */
data class EnvTemplateField(
    val key: String = "",
    val type: EnvFieldType = EnvFieldType.ANY,
)

