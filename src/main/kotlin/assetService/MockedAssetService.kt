package com.example.printscriptservice.assetService

import org.springframework.web.multipart.MultipartFile

class MockedAssetService(val bucketUrl: String): IAssetService {
    override fun getSnippet(snippetID: String): String {
        return "let a=1; let b=2; let c=a+b; console.log(c);" // ponele...
    }

}
