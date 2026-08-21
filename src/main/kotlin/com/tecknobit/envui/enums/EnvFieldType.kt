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
        validator = Regex(".*", RegexOption.DOT_MATCHES_ALL)
    ),

    ANY(
        displayName = "Any",
        validator = Regex(".*", RegexOption.DOT_MATCHES_ALL)
    )
}