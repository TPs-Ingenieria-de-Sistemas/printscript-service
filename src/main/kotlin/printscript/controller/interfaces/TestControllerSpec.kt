package com.example.printscriptservice.printscript.controller.interfaces

import TestCaseDTO.RunTestCaseDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/test")
interface TestControllerSpec {

    @PostMapping("/execute")
    fun testExecute(
        @RequestBody testCaseDTO: RunTestCaseDTO
    ): Boolean

}
