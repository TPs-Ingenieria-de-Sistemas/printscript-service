package com.example.printscriptservice.printscript.controller.implementations

import com.example.printscriptservice.printscript.controller.interfaces.ControllerSpec
import com.example.printscriptservice.printscript.model.ExecuteInput
import com.example.printscriptservice.printscript.model.FormatterInput
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
    override fun execute(input: ExecuteInput): ResponseEntity<String> {
        return service.execute(input.version, input.file, input.envs, input.inputs)
    }

    override fun format(input: FormatterInput): ResponseEntity<String> {
        return service.format(input.version, input.file, input.config)
    }
}
