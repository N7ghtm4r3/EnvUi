package com.tecknobit.envui.enums

enum class EnvFieldType(
    val displayName: String,
    val validator: Regex,
) {

    STRING(
        displayName = "String",
        validator = Regex(".*")
    ),

    INTEGER(
        displayName = "Integer",
        validator = Regex("-?\\d+")
    ),

    LONG(
        displayName = "Long",
        validator = Regex("-?\\d+")
    ),

    FLOAT(
        displayName = "Float",
        validator = Regex("-?(?:\\d+\\.?\\d*|\\.\\d+)")
    ),

    DOUBLE(
        displayName = "Double",
        validator = Regex("-?(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][+-]?\\d+)?")
    ),

    JSON(
        displayName = "Json",
        validator = Regex("""\{.*}""", RegexOption.DOT_MATCHES_ALL)
    ),

    ANY(
        displayName = "Any",
        validator = Regex(".*", RegexOption.DOT_MATCHES_ALL)
    );

    companion object {

        private val SINGLE_LINE_JSON_REGEX = Regex("\\s*\\n\\s*")

        private val MULTI_LINE_JSON_REGEX = Regex("""[{},]""")

        fun String.formatAsSingleLineJson(): String {
            return replace(SINGLE_LINE_JSON_REGEX, "")
        }

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

    }

}