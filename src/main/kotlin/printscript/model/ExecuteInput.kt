package com.example.printscriptservice.printscript.model

data class ExecuteInput(
    val version: String,
    val file: String,
    val envs: List<String>,
    val inputs: List<String>
)
