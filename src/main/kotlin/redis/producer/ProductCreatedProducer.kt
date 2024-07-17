package com.example.printscriptservice.redis.producer

import kotlinx.coroutines.reactor.awaitSingle
import com.example.printscriptservice.redis.consumer.ProductCreated
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class ProductCreatedProducer @Autowired constructor(
    @Value("\${stream.key}") streamKey: String,
    redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamKey, redis) {

    suspend fun publishEvent(name: String) {
        println("Publishing on stream: $streamKey")
        val product = ProductCreated(name)
        emit(product).awaitSingle()
    }
}
