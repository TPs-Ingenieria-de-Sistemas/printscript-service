// VERY IMPORTANT TO BE IN THIS PACKAGE
package com.example.printscriptservice.printscript.controller.interfaces

import com.example.printscriptservice.printscript.model.ExecuteInput
import com.example.printscriptservice.printscript.model.FormatterInput
import interpreter.Interpreter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream

@RequestMapping("/command")
interface ControllerSpec {

    // Usage: POST http://localhost:8080/command/lint with JSON body

    @PostMapping("/lint")
    fun lint(@RequestParam version: String, @RequestParam("file") file: String, @RequestParam config: String): ResponseEntity<String>

    @PostMapping("/execute")
    fun execute(@RequestBody input: ExecuteInput): ResponseEntity<String>

    @PostMapping("/format")
    fun format(@RequestBody input: FormatterInput): ResponseEntity<String>


}
