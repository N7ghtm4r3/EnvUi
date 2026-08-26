@file:OptIn(ExperimentalJewelApi::class)

package com.tecknobit.envui.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextArea
import org.jetbrains.jewel.ui.component.TextField
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * `DefaultDebounceInputShape` the default rounded shape applied to debounced inputs
 */
val DefaultDebounceInputShape = RoundedCornerShape(
    size = 4.dp
)

/**
 * Component used to collect validated text and emit it after a debounce delay
 *
 * @param modifier The modifier to apply to the input
 * @param shape The shape of the input
 * @param delay The delay before emitting the current value
 * @param onDebounce The callback invoked with the debounced text
 * @param validator The optional condition used to accept text changes
 * @param initialValue The initial text displayed by the input
 * @param placeholder The optional localized placeholder
 * @param keyboardOptions The keyboard configuration of the input
 * @param maxLines The maximum number of lines supported by the input
 */
@Composable
fun DebouncedInput(
    modifier: Modifier = Modifier,
    shape: Shape = DefaultDebounceInputShape,
    delay: Duration = 500.milliseconds,
    onDebounce: (String) -> Unit,
    validator: ((TextFieldValue) -> Boolean)? = null,
    initialValue: String,
    placeholder: StringResource? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
) {
    val value = remember {
        mutableStateOf(
            TextFieldValue(
                text = initialValue
            )
        )
    }

    LaunchedEffect(value.value.text) {
        delay(delay)
        onDebounce(value.value.text)
    }

    val modifier = modifier
        .clip(shape)

    val placeholderContent: @Composable () -> Unit = {
        if (placeholder != null) {
            Text(
                text = stringResource(placeholder)
            )
        }
    }

    val onValueChange: (TextFieldValue) -> Unit = { textFieldValue ->
        val isValid = validator == null || validator.invoke(textFieldValue)

        if (isValid || textFieldValue.text.isBlank())
            value.value = textFieldValue
    }

    if (maxLines == 1) {
        SimpleDebouncedField(
            modifier = modifier,
            onValueChange = onValueChange,
            placeholder = placeholderContent,
            keyboardOptions = keyboardOptions,
            value = value
        )
    } else {
        AreaDebouncedField(
            modifier = modifier,
            onValueChange = onValueChange,
            value = value,
            placeholder = placeholderContent,
            keyboardOptions = keyboardOptions,
        )
    }
}

/**
 * Component used to display a single-line debounced text field
 *
 * @param modifier The modifier to apply to the field
 * @param onValueChange The callback invoked when the text changes
 * @param value The mutable text field value
 * @param placeholder The optional placeholder content
 * @param keyboardOptions The keyboard configuration of the field
 */
@Composable
private fun SimpleDebouncedField(
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
    value: MutableState<TextFieldValue>,
    placeholder: (@Composable () -> Unit)?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    TextField(
        modifier = modifier,
        value = value.value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        keyboardOptions = keyboardOptions
    )
}

/**
 * Component used to display a multiline debounced text area
 *
 * @param modifier The modifier to apply to the area
 * @param onValueChange The callback invoked when the text changes
 * @param value The mutable text field value
 * @param placeholder The optional placeholder content
 * @param keyboardOptions The keyboard configuration of the area
 * @param maxLines The maximum number of displayed lines
 */
@Composable
private fun AreaDebouncedField(
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
    value: MutableState<TextFieldValue>,
    placeholder: (@Composable () -> Unit)?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = Int.MAX_VALUE,
) {
    TextArea(
        modifier = modifier
            .height(120.dp),
        decorationBoxModifier = Modifier
            .padding(
                horizontal = 8.dp,
                vertical = 6.dp
            ),
        value = value.value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        keyboardOptions = keyboardOptions,
        maxLines = maxLines
    )
}