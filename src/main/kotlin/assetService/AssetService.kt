package com.example.printscriptservice.assetService

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class AssetService(val bucketUrl: String) : IAssetService {

    private val client = OkHttpClient()

    override fun getSnippet(snippetID: String, name: String): Result<String> {
        val url = if (bucketUrl.endsWith("/")) "$bucketUrl$snippetID/$name" else "$bucketUrl/$snippetID/$name"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return Result.failure(Exception("Unexpected code $response"))

            if(!response.body?.string().isNullOrEmpty())
                Result.success(response.body?.string())

            return Result.failure(Exception("Empty response body"))
        }
    }

}
