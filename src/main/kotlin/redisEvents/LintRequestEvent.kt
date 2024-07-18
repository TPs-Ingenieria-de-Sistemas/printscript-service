package com.example.printscriptservice.redisEvents

data class LintRequestEvent(
    val userID: String,
    val snippetID: String,
    val rules: String, // Le pasa el YSON o YAML como string y lo construyo acá.
    val language: String,
    val version: String,
)
