package com.example.printscriptservice.assetService

import org.springframework.web.multipart.MultipartFile


// We use this to access the snippets in the bucket
interface IAssetService {

    fun getSnippet(snippetID: String): String
}
