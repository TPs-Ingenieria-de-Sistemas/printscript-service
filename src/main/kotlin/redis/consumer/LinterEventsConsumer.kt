package com.example.printscriptservice.redis.consumer

import com.example.printscriptservice.assetService.AssetService
import com.example.printscriptservice.assetService.MockedAssetService
import com.example.printscriptservice.printscript.service.implementations.Service
import com.example.printscriptservice.redis.producer.LinterEventsProducer
import com.example.printscriptservice.redisEvents.LintRequestEvent
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import redisStreams.RedisStreamConsumer
import java.time.Duration

@Component
class LinterEventsConsumer @Autowired constructor(
    redis: RedisTemplate<String, String>,
    @Value("\${redis.stream.linter-request-key}") streamKey: String,
    @Value("\${redis.groups.lint}") groupId: String,
    private val printscriptService: Service,

    private val producer: LinterEventsProducer
) : RedisStreamConsumer<LintRequestEvent>(streamKey, groupId, redis) {

    private val logger: Logger = LoggerFactory.getLogger(Service::class.java)


    // Este método verifica cada 10 segundos. Dice que los mensajes se deserializarán en forma de LintRequestEvent
    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, LintRequestEvent>> {
        logger.info("OPTIONS")
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(10000)) // Set poll rate
            .targetType(LintRequestEvent::class.java) // Set type to de-serialize record
            .build()
    }

    // Cada vez que llega un request.
    override fun onMessage(record: ObjectRecord<String, LintRequestEvent>) {

        logger.info("Received Message from Redis")
        val req = record.value
        println(req.userID + " " + req.snippetID + " " + req.rules + " " + req.language + " " + req.version)
        /*val assetSer = AssetService("http://localhost:8081/snippets")*/
        val assetSer = MockedAssetService("http://localhost:8082/snippets")
        val snippet = assetSer.getSnippet(req.userID, req.snippetName)

        snippet.onFailure {
            e ->
            run {
                logger.error("Error while getting snippet for linting: $e")
                runBlocking {
                    producer.publishEvent(req.userID, req.snippetID, "$e")
                }
            }
        }.onSuccess {
            logger.info("Linting Request")
            val lintRes: ResponseEntity<String> = printscriptService.lint(req.version, it, req.rules)

            if(!isPrintscript(req.language)){
                logger.warn("Unknown language")
                runBlocking {
                    producer.publishEvent(req.userID, req.snippetID, "Unknown language")
                }
            }
            else if (lintRes.statusCode.is2xxSuccessful) {
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
    }

    private fun bodyIsEmpty(response: ResponseEntity<String>): Boolean {
        return response.body.isNullOrEmpty()
    }

    private fun isPrintscript(language: String): Boolean
    {
        return when (language) {
            "printscript", "ps", "PrintScript", "printScript" -> true
            else -> false
        }
    }
}
