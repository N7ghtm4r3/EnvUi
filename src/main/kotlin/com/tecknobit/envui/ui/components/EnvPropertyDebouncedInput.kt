package com.tecknobit.envui.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.enter_env_value_placeholder
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.utils.converters.toKeyboardType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EnvPropertyDebounceInput(
    modifier: Modifier = Modifier,
    shape: Shape = DefaultDebounceInputShape,
    delay: Duration = 500.milliseconds,
    onDebounce: (String) -> Unit,
    property: Property,
    type: EnvFieldType,
) {
    val valueEntry = property.valueEntry
    val value = valueEntry?.text ?: ""

    DebouncedInput(
        modifier = modifier,
        shape = shape,
        delay = delay,
        initialValue = value,
        placeholder = Res.string.enter_env_value_placeholder,
        onDebounce = onDebounce,
        validator = { textFieldValue ->
            type.validator.matches(textFieldValue.text)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = type.toKeyboardType()
        )
    )
}
