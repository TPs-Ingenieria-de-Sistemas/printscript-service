package com.example.printscriptservice.controller.implementations

import com.example.printscriptservice.controller.interfaces.ControllerSpec
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import com.example.printscriptservice.service.implementations.Service
import java.io.File


@RestController
class Controller : ControllerSpec {

    val service = Service();

    override fun lint(file: MultipartFile, configJSON: MultipartFile): ResponseEntity<String> {
        return service.lint(file, configJSON)
    }

    override fun execute(file: MultipartFile): ResponseEntity<String> {
        return service.execute(file)
    }

    override fun format(file: MultipartFile, configJSON: MultipartFile): ResponseEntity<File> {
        return service.format(file, configJSON)
    }
}
