package com.example.printscriptservice.printscript.service.interfaces

import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import interpreter.Interpreter
import java.io.BufferedInputStream

interface ServiceInterface {
    // Después, otro, lo transformará a CLIKT???
    fun lint(version: String, file: String, config: String): ResponseEntity<String>
    fun execute(version: String, stream: String, envs: List<String>, inputs: List<String>): ResponseEntity<String>
    fun format(version: String, snippet: String, configStr: String): ResponseEntity<String>
}
