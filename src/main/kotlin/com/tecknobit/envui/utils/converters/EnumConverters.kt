package com.tecknobit.envui.utils.converters

import androidx.compose.ui.text.input.KeyboardType
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.enums.EnvFieldType.*

/**
 * Method used to convert this environment field type into its keyboard type
 *
 * @return the keyboard type associated with the field as [KeyboardType]
 */
fun EnvFieldType.toKeyboardType(): KeyboardType {
    return when(this) {
        STRING, BOOLEAN -> KeyboardType.Text
        INTEGER, LONG -> KeyboardType.Number
        FLOAT, DOUBLE -> KeyboardType.Decimal
        JSON -> KeyboardType.Text
        ANY -> KeyboardType.Unspecified
    }
}