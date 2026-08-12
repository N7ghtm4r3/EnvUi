package com.tecknobit.envui.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tecknobit.envui.ide.envfile.Property
import org.jetbrains.jewel.ui.component.Text

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