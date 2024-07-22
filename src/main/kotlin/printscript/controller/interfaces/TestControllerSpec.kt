package com.example.printscriptservice.printscript.controller.interfaces

import TestCaseDTO.RunTestCaseDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/test")
interface TestControllerSpec {
    @PostMapping("/")
    fun testExecute(
        testCaseDTO: RunTestCaseDTO
    ): Boolean

}
