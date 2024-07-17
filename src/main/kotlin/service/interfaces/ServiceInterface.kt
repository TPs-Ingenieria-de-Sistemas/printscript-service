package com.example.printscriptservice.service.interfaces

import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import interpreter.Interpreter
import java.io.BufferedInputStream

interface ServiceInterface {
    // Después, otro, lo transformará a CLIKT???
    fun lint(version: String, file: MultipartFile, config: MultipartFile): ResponseEntity<String>
    fun execute(version: String, stream: BufferedInputStream): ResponseEntity<Interpreter>
    fun format(version: String, file: MultipartFile, config: MultipartFile): ResponseEntity<String>
}
