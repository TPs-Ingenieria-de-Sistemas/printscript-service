// VERY IMPORTANT TO BE IN THIS PACKAGE
package com.example.printscriptservice.printscript.controller.interfaces

import com.example.printscriptservice.printscript.model.ExecuteInput
import com.example.printscriptservice.printscript.model.FormatterInput
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/command")
interface ControllerSpec {
    // Usage: POST http://localhost:8080/command/lint with JSON body

    // I think maybe it should recieve a MultiPartFile instead of a Json
    @PostMapping("/lint")
    fun lint(
        @RequestParam version: String,
        @RequestParam("file") file: String,
        @RequestParam config: String,
    ): ResponseEntity<String>

    @PostMapping("/execute")
    fun execute(
        @RequestBody input: ExecuteInput,
    ): ResponseEntity<String>

    @PostMapping("/format")
    fun format(
        @RequestBody input: FormatterInput,
    ): ResponseEntity<String>
}
