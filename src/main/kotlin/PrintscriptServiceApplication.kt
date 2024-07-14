package com.example.printscriptservice

import com.example.printscriptservice.service.implementations.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class PrintscriptServiceApplication

private val logger: Logger = LoggerFactory.getLogger(Service::class.java)

fun main(args: Array<String>) {
    logger.info("STARTING THE APPLICATION")
    SpringApplication.run(PrintscriptServiceApplication::class.java, *args)
}
