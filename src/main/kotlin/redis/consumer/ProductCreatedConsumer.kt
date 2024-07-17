package com.example.printscriptservice.redis.consumer

import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ProductCreatedConsumer @Autowired constructor(
    redis: ReactiveRedisTemplate<String, String>,
    @Value("\${stream.key}") streamKey: String,
    @Value("\${groups.product}") groupId: String
) : RedisStreamConsumer<ProductCreated>(streamKey, groupId, redis) {

    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, ProductCreated>> {
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(10000)) // Set poll rate
            .targetType(ProductCreated::class.java) // Set type to de-serialize record
            .build();
    }

    override fun onMessage(record: ObjectRecord<String, ProductCreated>) {
        // What we want to do with the stream
        Thread.sleep(1000 * 10)
        println("Id: ${record.id}, Value: ${record.value}, Stream: ${record.stream}, Group: ${groupId}")
    }
}
