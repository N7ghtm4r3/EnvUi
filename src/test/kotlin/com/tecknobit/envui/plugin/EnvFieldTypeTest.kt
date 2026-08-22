package com.tecknobit.envui.plugin

import com.tecknobit.envui.enums.EnvFieldType
import com.tecknobit.envui.enums.EnvFieldType.Companion.formatAsMultiLineJson
import com.tecknobit.envui.enums.EnvFieldType.Companion.formatAsSingleLineJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EnvFieldTypeTest {

    @Test
    fun `integer and long accept signed whole numbers only`() {
        listOf(EnvFieldType.INTEGER, EnvFieldType.LONG).forEach { type ->
            assertTrue(type.validator.matches("0"))
            assertTrue(type.validator.matches("-42"))
            assertFalse(type.validator.matches("+42"))
            assertFalse(type.validator.matches("4.2"))
            assertFalse(type.validator.matches(""))
        }
    }

    @Test
    fun `float accepts decimal forms but not scientific notation`() {
        listOf("1", "1.", ".5", "-0.25").forEach { value ->
            assertTrue(EnvFieldType.FLOAT.validator.matches(value))
        }

        listOf(".", "1e3", "+1.0", "").forEach { value ->
            assertFalse(EnvFieldType.FLOAT.validator.matches(value))
        }
    }

    @Test
    fun `double additionally accepts scientific notation`() {
        listOf("1", ".5", "-2.5e-3", "6E+4").forEach { value ->
            assertTrue(EnvFieldType.DOUBLE.validator.matches(value))
        }

        listOf("1e", "e3", "+2.0", "").forEach { value ->
            assertFalse(EnvFieldType.DOUBLE.validator.matches(value))
        }
    }

    @Test
    fun `json requires an object while any accepts multiline content`() {
        assertTrue(EnvFieldType.JSON.validator.matches("{\"enabled\":true}"))
        assertTrue(EnvFieldType.JSON.validator.matches("{\n  \"enabled\": true\n}"))
        assertFalse(EnvFieldType.JSON.validator.matches("[1, 2, 3]"))
        assertFalse(EnvFieldType.JSON.validator.matches("plain text"))

        assertTrue(EnvFieldType.ANY.validator.matches("first line\nsecond line"))
        assertFalse(EnvFieldType.STRING.validator.matches("first line\nsecond line"))
    }

    @Test
    fun `json formatting converts between compact and indented representations`() {
        val multiline = """{
            "name": "EnvUi",
            "enabled": true
        }""".trimIndent()

        assertEquals("{\"name\": \"EnvUi\",\"enabled\": true}", multiline.formatAsSingleLineJson())
        assertEquals(
            "{\n    \"name\":\"EnvUi\",\n    \"enabled\":true\n}",
            "{\"name\":\"EnvUi\",\"enabled\":true}".formatAsMultiLineJson()
        )
    }

}
