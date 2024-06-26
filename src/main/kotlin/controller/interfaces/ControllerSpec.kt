// VERY IMPORTANT TO BE IN THIS PACKAGE
package com.example.printscriptservice.controller.interfaces

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File


// AS FOR NOW I'M USING ALL LOCAL FILES SO THERE IS NO TROUBLE
// IN THE FUTURE IT SHOULD RECEIVE NOT A FILE BUT A STREAM.


// que sea una interfaz
@RequestMapping("/command")
interface ControllerSpec {

    /*// Usage: GET http://localhost:8080/command/lint
    @GetMapping("/lint")
    @Operation(
        summary = "Lint a snippet",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Linting completed",
                content = [Content(mediaType = "text/plain", schema = Schema(type = "string"))]
            )
        ]
    )
    fun lint(@RequestParam snippet: String): String*/

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
    fun lint(@RequestParam("file") file: MultipartFile, @RequestParam configJSON: MultipartFile): ResponseEntity<String>

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
    fun execute(@RequestParam("file") file: MultipartFile): ResponseEntity<String>

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
    fun format(@RequestParam("file") file: MultipartFile, @RequestParam configJSON: MultipartFile): ResponseEntity<File>


}
