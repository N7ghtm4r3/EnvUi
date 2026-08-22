package com.tecknobit.envui.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.enums.EnvFieldType.Companion.formatAsMultiLineJson
import com.tecknobit.envui.enums.EnvFieldType.JSON
import com.tecknobit.envui.generated.resources.Res
import com.tecknobit.envui.generated.resources.enter_env_value_placeholder
import com.tecknobit.envui.ide.envfile.Property
import com.tecknobit.envui.utils.converters.toKeyboardType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Component used to edit an environment property value according to its declared field type
 *
 * @param modifier The modifier to apply to the input
 * @param shape The shape of the input
 * @param delay The delay before emitting the current value
 * @param onDebounce The callback invoked with the validated debounced value
 * @param property The environment property edited by the input
 * @param type The expected value type of the property
 */
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
    var value = valueEntry?.text ?: ""
    val isJsonField = type == JSON
    if (isJsonField)
        value = value.formatAsMultiLineJson()

    DebouncedInput(
        modifier = modifier,
        shape = shape,
        delay = delay,
        initialValue = value,
        placeholder = Res.string.enter_env_value_placeholder,
        onDebounce = onDebounce,
        validator = { textFieldValue ->
            isJsonField || type.validator.matches(textFieldValue.text)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = type.toKeyboardType()
        ),
        maxLines = if (isJsonField)
            10
        else
            1
    )
}