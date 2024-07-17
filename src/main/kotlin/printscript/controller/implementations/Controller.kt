package com.example.printscriptservice.printscript.controller.implementations

import com.example.printscriptservice.printscript.controller.interfaces.ControllerSpec
import com.example.printscriptservice.printscript.model.LintInput
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import com.example.printscriptservice.printscript.service.implementations.Service
import interpreter.Interpreter
import java.io.BufferedInputStream


@RestController
class Controller : ControllerSpec {

    val service = Service();

    // Como no sé si el snippet service soporta el List<WarningResult> que devuelve el linter, lo mando como String.
    override fun lint(version: String, file: String, config: String): ResponseEntity<String>  {
        return service.lint(version, file, config)
    }

    // todo: it recieves a Stream, but could recieve a String and we turn it into a stream inside here.
    override fun execute(version: String, stream: BufferedInputStream): ResponseEntity<Interpreter> {
        return service.execute(version, stream)
    }

    override fun format(version: String, file: MultipartFile, config: MultipartFile): ResponseEntity<String> {
        return service.format(version, file, config)
    }
}
