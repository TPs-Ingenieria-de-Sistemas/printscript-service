package com.example.printscriptservice.printscript.controller.implementations

import TestCaseDTO.RunTestCaseDTO
import com.example.printscriptservice.printscript.controller.interfaces.TestControllerSpec

class TestController: TestControllerSpec {
    override fun testExecute(testCaseDTO: RunTestCaseDTO): Boolean {

        return true
    }
}
