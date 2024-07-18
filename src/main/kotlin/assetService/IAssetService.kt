package com.example.printscriptservice.assetService

// We use this to access the snippets in the bucket
interface IAssetService {
    fun getSnippet(snippetID: String): String
}
