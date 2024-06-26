package com.example.printscriptservice.service.interfaces

import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import java.io.File

interface ServiceInterface {
    fun lint(file:MultipartFile, configJSON:MultipartFile): ResponseEntity<String>
    fun execute(file:MultipartFile): ResponseEntity<String>
    fun format(file: MultipartFile, configJSON: MultipartFile): ResponseEntity<File>
}
