package com.example.printscriptservice.printscript.service.implementations

import com.example.printscriptservice.printscript.model.EnvVar
import interpreter.Interpreter
import java.io.BufferedInputStream
import java.nio.charset.Charset

fun main(args: Array<String>) {
    val outputs = runInterpreter(args)
    println(outputs.joinToString("\n"))
}

fun runInterpreter(args: Array<String>): List<String> {
    val version = args[0]
    val file = args[1]
    val inputs = args[2].split("\n")

    val envs = System.getenv().map { EnvVar(it.key, it.value) }

    val service = Service()

    val interpreter: Interpreter = service.interpretFile(version, file, inputs).getOrElse {
        logger.error("Error at interpreting file", it)
        return listOf("Error at interpreting file: ${it.message}")
    }

    val report = interpreter.report
    val response = mutableListOf<String>()
    report.outputs.forEach { out -> response.add(out) }

    if (report.errors.isNotEmpty()) {
        response.add("Found errors while executing:")
        report.errors.forEach { response.add(it) }
    }

    return response
}
