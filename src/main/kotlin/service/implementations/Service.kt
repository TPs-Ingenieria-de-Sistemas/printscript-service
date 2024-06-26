package com.example.printscriptservice.service.implementations

import com.example.printscriptservice.controller.implementations.Controller
import com.example.printscriptservice.service.interfaces.ServiceInterface
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import java.io.File

class Service : ServiceInterface {

    private val logger: Logger = LoggerFactory.getLogger(Controller::class.java)

    // creo q devuelve una lista en realidad. Además, debería devolverla como Stream

    override fun lint(file: MultipartFile, configJSON: MultipartFile): ResponseEntity<String> {
        if (!isOfExtension(file, ".ps")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The file must have a .ps extension")
        }
        if(!isOfExtension(configJSON, ".json")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The config file must have a .json extension")
        }

        val tempFile = createTempFile(file, ".ps")
        val tempConfigFile = createTempFile(configJSON, ".json")

        val output = captureOutput("analyzing", tempFile, tempConfigFile);

        tempFile.delete()
        tempConfigFile.delete()

        return ResponseEntity.ok(output)
    }

    // devuelve el resultado de la ejecución
    override fun execute(file: MultipartFile): ResponseEntity<String> {
        if (!isOfExtension(file, ".ps")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The file must have a .ps extension")
        }

        // Save the file and configJSON to temporary files
        val tempFile = createTempFile(file, ".ps")

        val output = captureOutput("execute", tempFile);

        return ResponseEntity.ok(output)
    }

    // should return the original file with the formatting applied.
    override fun format(file: MultipartFile, configJSON: MultipartFile): ResponseEntity<File> {
        if (!isOfExtension(file, ".ps")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
        if(!isOfExtension(configJSON, ".json")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }

        val tempFile = createTempFile(file, ".ps")
        val tempConfigFile = createTempFile(configJSON, ".json")

        captureOutput("formatting", tempFile, tempConfigFile);

        tempConfigFile.delete()
        try {
            return ResponseEntity.ok(tempFile)
        }
        finally{
            tempFile.delete()
        }
    }

    private fun isOfExtension(file: MultipartFile, extension: String): Boolean {
        return file.originalFilename?.endsWith(extension) == true
    }

    private fun createTempFile(file: MultipartFile, extension: String): File {
        val tempFile = File.createTempFile("file", extension)
        file.transferTo(tempFile)
        return tempFile
    }

    private fun captureOutput(command: String, tempFile: File): String {
        val processBuilder = ProcessBuilder("./gradlew", "run", "--args=$command ${tempFile.absolutePath}")
        processBuilder.redirectErrorStream(true)

        return runCommand(processBuilder);
    }

    private fun captureOutput(command: String, tempFile: File, jsonConfig: File): String {
        val processBuilder = ProcessBuilder("./gradlew", "run", "--args=$command ${tempFile.absolutePath} ${jsonConfig.absolutePath}")
        processBuilder.redirectErrorStream(true)

        return try {
            runCommand(processBuilder)
        } catch (e: Exception) {
            logger.error("Error executing command: $command", e)
            "Error executing command: $command"
        }
    }

    private fun runCommand(processBuilder: ProcessBuilder): String {
        val process = processBuilder.start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        logger.info("Command executed successfully")
        return output
    }
}
