package com.example.printscriptservice.redis.producer

import com.example.printscriptservice.redisEvents.LintResultEvent
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class LinterEventsProducer
    @Autowired
    constructor(
        @Value("\${redis.stream.linter-producer-key}") streamKey: String,
        redis: ReactiveRedisTemplate<String, String>,
    ) : RedisStreamProducer(streamKey, redis) {
        // WOULD BE BETTER IF IT USED SUSPEND?
        suspend fun publishEvent(
            userID: String,
            snippetID: String,
            result: String,
        ) {
            println("Publishing on stream: $streamKey")
            val res = LintResultEvent(userID, snippetID, result)
            emit(res)
        }
    }
