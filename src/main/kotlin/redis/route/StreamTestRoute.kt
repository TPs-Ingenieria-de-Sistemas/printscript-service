package com.example.printscriptservice.redis.route
import com.example.printscriptservice.redis.producer.LinterEventsProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StreamTestRoute
    @Autowired
    constructor(private val producer: LinterEventsProducer) {
        @PostMapping("/lint-stream/{userID}/{snippetID}/{result}")
        suspend fun post(
            @PathVariable userID: String,
            @PathVariable snippetID: String,
            @PathVariable result: String,
        ) {
            producer.publishEvent(userID, snippetID, result)
        }
    }
