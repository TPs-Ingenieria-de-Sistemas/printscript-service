package com.example.printscriptservice.redis.producer

import redisEvents.LintResultEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import redisStreams.RedisStreamProducer

@Component
class LinterEventsProducer
    @Autowired
    constructor(
        @Value("\${redis.stream.linter-response-key}") streamKey: String,
        redis: RedisTemplate<String, String>,
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
