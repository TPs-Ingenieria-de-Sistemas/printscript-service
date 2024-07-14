package springTest

import com.example.printscriptservice.service.implementations.LanguageFactory
import com.example.printscriptservice.service.implementations.Service
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test




// For testing it is using local files. Dunno if it is correct.
class ServiceTest {

    private val service = Service()
    private val languageFactory = LanguageFactory("printscript")
    private val aux = TestAuxMethods()

    @Test
    fun testLint() {

        val absFile = aux.accessAbsolutePath("src/test/resources/linter/no-warnings-file.ps")
        val absConfig = aux.accessAbsolutePath("src/test/resources/linter/linterConfig.json")
        val file = aux.getFile(absFile)
        val config = aux.getFile(absConfig)
        val result = service.lint("1.1", file, config)
        assertTrue(result.statusCode.is2xxSuccessful)
    }
    /*
    @Test
    fun testExecute() {
        val file = getFile("src/test/resources/test.ps")
        val result = service.execute("1.1", file.inputStream.buffered())
        assertTrue(result.body!!.contains("Execution finished"))
    }

    @Test
    fun testFormat() {
        val file = getFile("src/test/resources/test.ps")
        val config = getFile("src/test/resources/testConfig.ps")
        val result = service.format("1.1", file, config)
        assertTrue(result.body!!.contains("Code formatted successfully"))
    }*/




}
