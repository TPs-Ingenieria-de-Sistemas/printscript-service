package springTest

import com.example.printscriptservice.printscript.service.implementations.LanguageFactory
import com.example.printscriptservice.printscript.service.implementations.Service
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.io.BufferedReader
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
    @Test
    fun testExecute() {
        val bufferedReader: BufferedReader = File("src/test/resources/code001.ps").bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        val result: ResponseEntity<String> = service.execute("1.1",  inputString, emptyList(), emptyList())
        assertTrue(result.statusCode.is2xxSuccessful)
    }


    @Test
    fun testFormat() {
        val file: BufferedReader = File("src/test/resources/code001.ps").bufferedReader()
        val config: BufferedReader = File("src/test/resources/formatter/formatterConfig.json").bufferedReader()
        val inputString = file.use { it.readText() }
        val configString = config.use { it.readText() }

        val result = service.format("1.1", inputString, configString)
        assertTrue(result.statusCode.is2xxSuccessful)
    }




}
