package com.example.printscriptservice

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootApplication
class PrintscriptServiceApplication

private val logger: Logger = LoggerFactory.getLogger(PrintscriptServiceApplication::class.java)

fun main(args: Array<String>) {
    logger.info("STARTING THE APPLICATION")
    SpringApplication.run(PrintscriptServiceApplication::class.java, *args)
}
