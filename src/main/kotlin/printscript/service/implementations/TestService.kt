package com.example.printscriptservice.printscript.service.implementations

import com.example.printscriptservice.printscript.model.ExecuteInput
import kotlin.math.log

@org.springframework.stereotype.Service
class TestService {

     fun executeTest(executeInput: ExecuteInput, expectedOutput: List<String>): Boolean {
        val service = Service()
        val output = service.execute(executeInput.version, executeInput.file, executeInput.envs, executeInput.inputs)

        if (!output.statusCode.is2xxSuccessful)
            return false

        val outputList = output.body?.split("\n")?.filter { it.isNotBlank() }

        if (outputList.isNullOrEmpty() && expectedOutput.isEmpty()){
            logger.info("Output is empty and expected output is empty")
            return true}
        else if (outputList.isNullOrEmpty() && expectedOutput.isNotEmpty())
            return false
        else if (outputList != null && outputList.size != expectedOutput.size)
            return false

        logger.info("Output: $outputList")
        return outputList?.let { it == expectedOutput } ?: false
    }
}
