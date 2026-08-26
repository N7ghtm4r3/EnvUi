package com.tecknobit.envui.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tecknobit.envui.ide.envfile.Property
import org.jetbrains.jewel.ui.component.Text

/**
 * Component used to display the key of an environment property
 *
 * @param modifier The modifier to apply to the text
 * @param fontSize The size applied to the key
 * @param property The environment property whose key is displayed
 */
@Composable
fun KeyText(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    property: Property
) {
    KeyText(
        modifier = modifier,
        fontSize = fontSize,
        key = property.keyEntry.text,
    )
}

/**
 * Component used to display an environment property key with bold emphasis
 *
 * @param modifier The modifier to apply to the text
 * @param fontSize The size applied to the key
 * @param key The environment property key to display
 */
@Composable
fun KeyText(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    key: String,
) {
    Text(
        modifier = modifier,
        text = key,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize
    )
}