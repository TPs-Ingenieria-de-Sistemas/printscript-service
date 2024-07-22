package com.example.printscriptservice.printscript.service.interfaces

import com.example.printscriptservice.printscript.model.EnvVar
import org.springframework.http.ResponseEntity

interface ServiceInterface {
    // Después, otro, lo transformará a CLIKT???
    fun lint(
        version: String,
        file: String,
        config: String,
    ): ResponseEntity<String>

    fun execute(
        version: String,
        file: String,
        envs: List<EnvVar>,
        inputs: List<String>,
    ): ResponseEntity<String>

    fun format(
        version: String,
        snippet: String,
        configStr: String,
    ): ResponseEntity<String>
}
