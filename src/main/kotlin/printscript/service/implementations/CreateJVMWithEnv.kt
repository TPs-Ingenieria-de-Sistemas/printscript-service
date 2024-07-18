package com.example.printscriptservice.printscript.service.implementations

import com.example.printscriptservice.printscript.model.EnvVar
import interpreter.Interpreter
import java.io.BufferedInputStream
import java.nio.charset.Charset

fun main(args: Array<String>) {
    val version = args[0]
    val file = args[1]
    val inputs = args.drop(2)

    val envs = System.getenv().map { EnvVar(it.key, it.value) }

    val service = Service()

    val interpreter: Interpreter = service.interpretFile(version, file, inputs).getOrElse {
        logger.error("Error at interpreting file", it)
        return
    }

    logger.info("getting report")
    val report = interpreter.report
    val response = StringBuilder()
    report.outputs.forEach { out -> println(response.append(out))}

    if (report.errors.isNotEmpty()) {
        println("Found errors while executing:")
        report.errors.forEach { println(it) }
    }
}


