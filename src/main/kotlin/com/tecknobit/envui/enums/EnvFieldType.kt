package com.tecknobit.envui.enums

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * The `EnvFieldType` enum is useful to represent the supported environment field value types
 *
 * @property displayName The name used to display the field type
 * @property validator The regular expression used to validate values of the field type
 * @property parser The parser used to check whether a value matches the field type
 *
 * @author N7ghtm4r3 - Tecknobit
 */
enum class EnvFieldType(
    val displayName: String,
    val validator: Regex,
    val parser: (Any) -> Boolean,
) {

    /**
     * The textual field type
     */
    STRING(
        displayName = "String",
        validator = Regex(".*"),
        parser = { true }
    ),

    /**
     * The integer numeric field type
     */
    INTEGER(
        displayName = "Integer",
        validator = Regex("-?\\d+"),
        parser = { it.toString().toIntOrNull() != null }
    ),

    /**
     * The boolean field type
     *
     * @since 1.0.1
     */
    BOOLEAN(
        displayName = "Boolean",
        validator = Regex("^(?:t(?:r(?:ue?)?)?|f(?:a(?:l(?:se?)?)?)?)$"),
        parser = { it.toString().toBooleanStrictOrNull() != null }
    ),

    /**
     * The long integer numeric field type
     */
    LONG(
        displayName = "Long",
        validator = Regex("-?\\d+"),
        parser = { it.toString().toLongOrNull() != null }
    ),

    /**
     * The floating-point numeric field type
     */
    FLOAT(
        displayName = "Float",
        validator = Regex("-?(?:\\d+\\.?\\d*|\\.\\d+)"),
        parser = { it.toString().toFloatOrNull() != null }
    ),

    /**
     * The double-precision numeric field type
     */
    DOUBLE(
        displayName = "Double",
        validator = Regex("-?(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][+-]?\\d+)?"),
        parser = { it.toString().toDoubleOrNull() != null }
    ),

    /**
     * The `JSON` object field type
     */
    JSON(
        displayName = "Json",
        validator = Regex("""\{.*}""", RegexOption.DOT_MATCHES_ALL),
        parser = {
            try {
                val jsonElement: JsonElement = Json.decodeFromString(it.toString())

                ((jsonElement is JsonObject) || (jsonElement is JsonArray))
            } catch (_: Exception) {
                false
            }
        }
    ),

    /**
     * The unrestricted field type
     */
    ANY(
        displayName = "Any",
        validator = Regex(".*", RegexOption.DOT_MATCHES_ALL),
        parser = { true }
    );

    /**
     * The companion object allows to format `JSON` environment values and access field type detection priorities
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    companion object {

        /**
         * `SINGLE_LINE_JSON_REGEX` the regular expression used to remove line separators and surrounding spaces
         */
        private val SINGLE_LINE_JSON_REGEX = Regex("\\s*\\n\\s*")

        /**
         * `MULTI_LINE_JSON_REGEX` the regular expression used to locate structural `JSON` separators
         */
        private val MULTI_LINE_JSON_REGEX = Regex("""[{},]""")

        /**
         * Method used to format this `JSON` value on a single line
         *
         * @return the single-line `JSON` value as [String]
         */
        fun String.formatAsSingleLineJson(): String {
            return replace(SINGLE_LINE_JSON_REGEX, "")
        }

        /**
         * Method used to format this `JSON` value across indented lines
         *
         * @return the multiline `JSON` value as [String]
         */
        fun String.formatAsMultiLineJson(): String {
            return replace(MULTI_LINE_JSON_REGEX) {
                when (it.value) {
                    "{" -> "{\n    "
                    "," -> ",\n    "
                    "}" -> "\n}"
                    else -> it.value
                }
            }
        }

        /**
         * `prioritizedEntries` the field types ordered by value detection priority
         *
         * @since 1.0.1
         */
        val prioritizedEntries: List<EnvFieldType> = mutableListOf<EnvFieldType>().apply {
            add(BOOLEAN)
            add(INTEGER)
            add(LONG)
            add(DOUBLE)
            add(FLOAT)
            add(JSON)
            add(STRING)
            add(ANY)
        }
        
    }

}