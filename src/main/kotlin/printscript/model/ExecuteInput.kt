package com.example.printscriptservice.printscript.model

data class ExecuteInput(
    val version: String,
    val file: String,
    val envs: List<EnvVar>,
    val inputs: List<String>,
)

data class EnvVar(
    val name: String,
    val value: String,
)
