package com.example.printscriptservice.assetService

import java.net.HttpURLConnection
import java.net.URL

class AssetService(val bucketUrl: String) : IAssetService {

    override fun getSnippet(snippetID: String, name: String): String {
        val url = URL("$bucketUrl/$snippetID/$name")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream.bufferedReader().use { it.readText() }
            } else {
                throw RuntimeException("Failed to fetch snippet: $responseCode")
            }
        }
    }
}
