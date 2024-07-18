package com.example.printscriptservice.printscript.model

data class LintInput(
    val version: String,
    val file: String,
    val config: String, // POR EL MOMENTO ES SIEMPRE UN JSON
)
