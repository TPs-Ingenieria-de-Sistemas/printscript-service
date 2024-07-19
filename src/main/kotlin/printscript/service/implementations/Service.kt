package com.example.printscriptservice.printscript.service.implementations

import MyLinter
import com.example.printscriptservice.printscript.service.interfaces.ServiceInterface
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import stringReader.PartialStringReadingLexer
import java.io.BufferedInputStream
import interpreter.Interpreter
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.charset.Charset
import ast.Scope
import factory.InterpreterFactoryImpl
import factory.LexerFactoryImpl
import formater.astFormatter.ASTFormatter
import formater.configurationReader.ConfigurationReaderProvider
import interpreter.inputReaderImp.MockInputReader
import configurationReader.ConfigurationReaderProvider as LinterConfigReader
import parser.MyParser
import result.validation.WarningResult
import translateFormatterConfigurationToRules


@org.springframework.stereotype.Service
class Service : ServiceInterface {

    // Y si al logger lo pongo fuera de la clase, como variable global???
    private val logger: Logger = LoggerFactory.getLogger(Service::class.java)

    // si recibiese un stream hacer prácticamente lo mismo que lo que hace el execute. Por el momento recibe un file.
    override fun lint(version: String, file: String, config: String): ResponseEntity<String> {
        logger.info("Linting Code")

        val reader = LinterConfigReader().getReader("json").getOrElse {
            logger.error("Error at creating reader for linter: ", it)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it.message)
        }

        val tempFile = createConfigTempFile(file, "ps")
        val tempConfigFile = createConfigTempFile(config, "json")
        // logger.info("Config file: $tempConfigFile")

        val configurations =
            reader.readFileAndBuildRules(tempConfigFile).getOrElse {
                logger.error("Error at building configurations for linter: ", it)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it.message)
            }

        val rules = translateFormatterConfigurationToRules(configurations)
        val scope = lexAndParse(tempFile, version).getOrElse {
            logger.error("Error at lexing and parsing file", it)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it.message)
        }
        val warnings = MyLinter().lintScope(scope, rules)
        val output = warningsAsJSON(warnings)
        tempConfigFile.delete()
        if(warnings.isEmpty()){
            logger.info("Code linted successfully without any warnings")
            return ResponseEntity.ok("Code linted successfully without any warnings")
        }
        tempFile.delete()
        tempConfigFile.delete()
        return ResponseEntity.ok(output)
    }


    override fun execute(version: String, file: String, envs: List<String>, inputs: List<String>): ResponseEntity<String>{

        val interpreter: Interpreter = interpretFile(version, file, inputs).getOrElse {
            logger.error("Error at interpreting file", it)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it.message)
        }
        logger.info("getting report")
        val report = interpreter.report
        val response = StringBuilder()
        report.outputs.forEach { out -> response.append(out).append("\n") }

        if (report.errors.isNotEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Found errors in while executing:\n" + report.errors.joinToString("\n") { it })
        }

        return ResponseEntity.ok(response.toString())
    }

    fun interpretFile(version: String, file: String, inputs: List<String>):  Result<Interpreter>{
        logger.info("interpretingFile")
        val tempFile = createConfigTempFile(file, "ps")
        tempFile.inputStream().buffered().use { stream ->
            tempFile.delete()
            return interpretStream(version, stream, inputs)
        }

    }

    // Basically the same method the CLI uses. We may, eventually, not receive BufferedInputStream but a different, similar, data type.
    fun interpretStream(version: String, stream: BufferedInputStream, inputs: List<String>): Result<Interpreter> {
        logger.info("Executing snippet")
        val lexer = LexerFactoryImpl(version).create()
        val parser = MyParser()
        InterpreterFactoryImpl(version).create()
        var interpreter = Interpreter(inputReader = MockInputReader(inputs))
        val buffer = ByteArray(1046)
        var bytesRead = stream.read(buffer)
        var tokenizer = PartialStringReadingLexer(lexer)

        while (bytesRead != -1) {
            logger.info("Reading bytes")
            val textBlock = String(buffer, 0, bytesRead, Charset.defaultCharset())
            val tokenizerAndTokens = tokenizer.tokenizeString(textBlock)
            tokenizer = tokenizerAndTokens.first
            val tokens = tokenizerAndTokens.second
            logger.info("Tokens: $tokens")
            val ast = parser.parseTokens(tokens).getOrElse {
                logger.error("Error at parsing tokens", it)
                return Result.failure(it)
            }
            logger.info("parsed tokens")
            interpreter = interpreter.interpret(ast)
            logger.info("interpreted")
            bytesRead = stream.read(buffer)

        }
        logger.info("Snippet executed successfully")
        stream.close()
        return Result.success(interpreter)
    }

    override fun format(version: String, snippet: String, configStr: String): ResponseEntity<String> {

        logger.info("Formatting Code")
        val file = createConfigTempFile(snippet, "ps")
        val config = createConfigTempFile(configStr, "json")
        val lexer = LexerFactoryImpl(version).create()

        val reader = ConfigurationReaderProvider().getReader("json").getOrElse {
            logger.error("Error at creating reader for formatter: ", it)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it.message)
        }
        val configurations =
            reader.readFileAndBuildRules(config).getOrElse {
                logger.error("Error at building configurations for formatter: ", it)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it.message)
            }
        val code = file.inputStream().bufferedReader().use { it.readText() }
        val formatter = ASTFormatter(lexer, MyParser(), configurations)
        val formattedCode = formatter.formatString(code)

        file.delete()
        config.delete()
        logger.info("Code formatted successfully")
        return ResponseEntity.ok(formattedCode)
    }



    private fun isOfExtension(file: MultipartFile, extension: String): Boolean {
        return file.originalFilename?.endsWith(extension) ?: false
    }

    private fun createConfigTempFile(config: MultipartFile): File {
        val tempConfigFile = if (isOfExtension(config, ".json")) {
            File.createTempFile("config", ".json")
        } else {
            File.createTempFile("config", ".yaml")
        }

        config.inputStream.use { input ->
            tempConfigFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tempConfigFile
    }


    private fun createConfigTempFile(config: String, extension: String): File {
        val tempFile = File.createTempFile("config", ".$extension")
        tempFile.writeText(config)
        return tempFile
    }


    fun lexAndParse(file: File, version: String): Result<Scope> {
        val lexer = LexerFactoryImpl(version).create()
        val parser = MyParser()
        val text = file.inputStream().bufferedReader().use { it.readText() }
        logger.info(text)
        val tokens = lexer.tokenize(text)
        val ast = parser.parseTokens(tokens).getOrElse {
            return Result.failure(it)
        }
        return Result.success(ast)
    }

    private fun warningToString(warning: WarningResult): String {
        val range = warning.range
        val string = "\t[\n" +
                "\t\t\"range: Warning at range ${range.start} - ${range.end}\"\n" +
                "\t\t\"message: ${warning.message}\"\n" +
                "\t]"
        return string
    }
    private fun warningsAsJSON(warnings: List<WarningResult>): String {
        return "{\n" + warnings.joinToString("\n") { warningToString(it)  + ","} + "\n}"
    }
}
