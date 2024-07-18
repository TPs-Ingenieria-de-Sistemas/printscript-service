package com.example.printscriptservice.redis.consumer

import com.example.printscriptservice.assetService.MockedAssetService
import com.example.printscriptservice.printscript.service.implementations.Service
import com.example.printscriptservice.redis.producer.LinterEventsProducer
import com.example.printscriptservice.redisEvents.LintRequestEvent
import kotlinx.coroutines.runBlocking
import org.austral.ingsis.redis.RedisStreamConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.http.ResponseEntity
import java.time.Duration

class LinterEventsConsumer(
    redis: ReactiveRedisTemplate<String, String>,
    @Value("\${redis.stream.linter-consumer-key}") streamKey: String,
    @Value("\${groups.lint}") groupId: String,
    private val printscriptService: Service,
    private val producer: LinterEventsProducer,
) : RedisStreamConsumer<LintRequestEvent>(streamKey, groupId, redis) {
    private val logger: Logger = LoggerFactory.getLogger(Service::class.java)

    // Este método verifica cada 10 segundos. Dice que los mensajes se deserializarán en forma de LintRequestEvent
    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, LintRequestEvent>> {
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(10000)) // Set poll rate
            .targetType(LintRequestEvent::class.java) // Set type to de-serialize record
            .build()
    }

    // Cada vez que llega un request.
    override fun onMessage(record: ObjectRecord<String, LintRequestEvent>) {
        logger.info("Received Message from Redis")
        val req = record.value
        val assetSer = MockedAssetService("url")
        val snippet = assetSer.getSnippet(req.snippetID)

        logger.info("Linting Request")
        val lintRes: ResponseEntity<String> = printscriptService.lint(req.version, snippet, req.rules)

        if (lintRes.statusCode.is2xxSuccessful) {
            logger.info("Lint passed")
            runBlocking {
                lintRes.body?.let {
                    producer.publishEvent(req.userID, req.snippetID, it)
                    logger.info("Lint result: {user id: ${req.userID}}, {snippetId: ${req.snippetID}}, result: $it}")
                }
                if (bodyIsEmpty(lintRes)) {
                    producer.publishEvent(req.userID, req.snippetID, "")
                    logger.info("Lint result: {user id: ${req.userID}}, {snippetId: ${req.snippetID}}, result: ...")
                }
            }
        } else {
            logger.warn("Lint Failed")
            runBlocking {
                producer.publishEvent(req.userID, req.snippetID, "Unknown error while linting")
            }
        }
    }

    private fun bodyIsEmpty(response: ResponseEntity<String>): Boolean {
        return response.body.isNullOrEmpty()
    }
}
