@file:OptIn(ExperimentalJewelApi::class)

package com.tecknobit.envui.ui.components

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
import org.jetbrains.jewel.ui.component.TextField
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

val DefaultDebounceInputShape = RoundedCornerShape(
    size = 4.dp
)

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
) {
    var value by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialValue
            )
        )
    }

    LaunchedEffect(value.text) {
        delay(delay)
        onDebounce(value.text)
    }

    TextField(
        modifier = modifier
            .clip(shape),
        value = value,
        onValueChange = { textFieldValue ->
            val isValid = validator == null || validator.invoke(textFieldValue)
            if (isValid || textFieldValue.text.isBlank())
                value = textFieldValue
        },
        placeholder = {
            placeholder?.let {
                Text(
                    text = stringResource(placeholder)
                )
            }
        },
        keyboardOptions = keyboardOptions
    )
}
