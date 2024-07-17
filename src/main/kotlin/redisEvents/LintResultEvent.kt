package com.example.printscriptservice.redisEvents

data class LintResultEvent(
    val userID: String,
    val snippetID: String,
    val result: String, // El resultado es un JSON en forma de String.
)
