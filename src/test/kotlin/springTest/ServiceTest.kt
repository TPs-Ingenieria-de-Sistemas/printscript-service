package springTest

import com.example.printscriptservice.printscript.service.implementations.LanguageFactory
import com.example.printscriptservice.printscript.service.implementations.Service
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

// For testing it is using local files. Dunno if it is correct.
class ServiceTest {
    private val service = Service()
    private val languageFactory = LanguageFactory("printscript")
    private val aux = TestAuxMethods()

    @Test
    fun testLint() {
        val absFile = aux.accessAbsolutePath("src/test/resources/linter/no-warnings-file.ps")
        val absConfig = aux.accessAbsolutePath("src/test/resources/linter/linterConfig.json")
        val file = File(absFile)
        val config = File(absConfig)

        val snippet = file.inputStream().bufferedReader().use { it.readText() }
        val json = config.inputStream().bufferedReader().use { it.readText() }
        val result = service.lint("1.1", snippet, json)
        assertTrue(result.statusCode.is2xxSuccessful)
    }

    /*@Test
    fun testExecute() {
        val file = aux.getFile("src/test/resources/test.ps")
        val result = service.execute("1.1", file.inputStream.buffered())
        assertTrue(result.body!!.("Execution finished")) // ???

    }

    @Test
    fun testFormat() {
        val file = aux.getFile("src/test/resources/test.ps")
        val config = aux.getFile("src/test/resources/testConfig.ps")
        val result = service.format("1.1", file, config)
        assertTrue(result.body!!.contains("Code formatted successfully"))
    }*/
}
