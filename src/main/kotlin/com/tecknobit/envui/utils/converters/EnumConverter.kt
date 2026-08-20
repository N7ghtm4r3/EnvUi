package com.tecknobit.envui.utils.converters

import androidx.compose.ui.text.input.KeyboardType
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.enums.EnvFieldType.*

fun EnvFieldType.toKeyboardType(): KeyboardType {
    return when(this) {
        STRING -> KeyboardType.Text
        INTEGER -> KeyboardType.Number
        FLOAT, DOUBLE -> KeyboardType.Decimal
        JSON -> KeyboardType.Text
        ANY -> KeyboardType.Unspecified
    }
}