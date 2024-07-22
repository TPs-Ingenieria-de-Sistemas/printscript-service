package com.example.printscriptservice.printscript.controller.implementations

import TestCaseDTO.RunTestCaseDTO
import com.example.printscriptservice.printscript.controller.interfaces.TestControllerSpec
import com.example.printscriptservice.printscript.model.EnvVar
import com.example.printscriptservice.printscript.model.ExecuteInput
import com.example.printscriptservice.printscript.service.implementations.TestService
import com.example.printscriptservice.printscript.service.implementations.logger
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController: TestControllerSpec {
    override fun testExecute(testCaseDTO: RunTestCaseDTO): Boolean {

        val testService = TestService()
        val result: Boolean
        logger.info("DTO: content: ${testCaseDTO.content}, input: ${testCaseDTO.input}, output: ${testCaseDTO.output}")
        if(testCaseDTO.output.isNullOrEmpty())
            result = testService.executeTest(DtoToExecuteInput(testCaseDTO), listOf())
        else
            result =testService.executeTest(DtoToExecuteInput(testCaseDTO), testCaseDTO.output!!)

        return result
    }

    fun DtoToExecuteInput(dto: RunTestCaseDTO): ExecuteInput {
        val envs = dto.env?.split(";")?.map {
            val (name, value) = it.split("=")
            EnvVar(name, value)
        } ?: emptyList()

        return ExecuteInput(
            version = "1.1",
            file = dto.content ?: "",
            envs = envs,
            inputs = dto.input ?: emptyList()
        )
    }


}
