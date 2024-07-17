// VERY IMPORTANT TO BE IN THIS PACKAGE
package com.example.printscriptservice.printscript.controller.interfaces

import interpreter.Interpreter
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream

@RequestMapping("/command")
interface ControllerSpec {

    // Usage: POST http://localhost:8080/command/lint with JSON body

    @PostMapping("/lint")
    @Operation(
        summary = "Lint a snippet",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [Content(mediaType = "application/json", schema = Schema(type = "string"))]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Linting completed",
                content = [Content(mediaType = "application/json", schema = Schema(type = "string"))]
            ),
        ]
    )
    // I think maybe it should recieve a MultiPartFile instead of a Json
    fun lint(@RequestParam version: String, @RequestParam("file") file: MultipartFile, @RequestParam config: MultipartFile): ResponseEntity<String>

    @PostMapping("/execute")
    @Operation(
        summary = "Execute a snippet",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Execution completed",
                content = [Content(mediaType = "text/plain", schema = Schema(type = "string"))]
            )
        ]
    )
    fun execute(@RequestParam version: String, @RequestParam("file") stream: BufferedInputStream): ResponseEntity<Interpreter>

    @PostMapping("/format")
    @Operation(
        summary = "Format a snippet",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Formatting completed",
                content = [Content(mediaType = "text/plain", schema = Schema(type = "string"))]
            )
        ]
    )
    fun format(@RequestParam version: String, @RequestParam("file") file: MultipartFile, @RequestParam config: MultipartFile): ResponseEntity<String>


}
