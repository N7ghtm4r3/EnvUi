@file:OptIn(ExperimentalJewelApi::class)

package com.tecknobit.envui.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun DebouncedInput(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(
        size = 4.dp
    ),
    delay: Duration = 500.milliseconds,
    onDebounce: (String) -> Unit,
    initialValue: String,
    placeholder: StringResource? = null,
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
        onValueChange = { value = it },
        placeholder = {
            placeholder?.let {
                Text(
                    text = stringResource(placeholder)
                )
            }
        }
    )
}
