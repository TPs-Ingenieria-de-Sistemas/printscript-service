package com.example.printscriptservice.assetService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class AssetService
@Autowired
constructor(
    private val rest: RestTemplate,
    @Value("\${assetService}")
    private val bucketUrl: String,
){

    fun getAsset(userId: String, assetName: String): String {
        val url = bucketUrl + "snippet-" + userId + "-" + assetName;
        val result = rest.getForObject(url, String::class.java)
        return result!!
    }
}

