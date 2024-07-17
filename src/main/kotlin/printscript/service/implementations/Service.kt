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
import factory.LexerFactoryImpl
import formater.astFormatter.ASTFormatter
import formater.configurationReader.ConfigurationReaderProvider
import configurationReader.ConfigurationReaderProvider as LinterConfigReader
import parser.MyParser
import result.validation.WarningResult
import translateFormatterConfigurationToRules


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
        return ResponseEntity.ok(output)
    }


    // Basically the same method the CLI uses. We may, eventually, not receive BufferedInputStream but a different, similar, data type.
    override fun execute(version: String, stream: BufferedInputStream): ResponseEntity<Interpreter> {
        logger.info("Executing snippet")
        val lexer = LexerFactoryImpl(version).create()
        val parser = MyParser()
        var interpreter = Interpreter()
        val buffer = ByteArray(1046)
        var bytesRead = stream.read(buffer)
        var tokenizer = PartialStringReadingLexer(lexer)

        while (bytesRead != -1) {
            val textBlock = String(buffer, 0, bytesRead, Charset.defaultCharset())
            val tokenizerAndTokens = tokenizer.tokenizeString(textBlock)
            tokenizer = tokenizerAndTokens.first
            val tokens = tokenizerAndTokens.second

            val ast = parser.parseTokens(tokens).getOrElse {
                logger.error("Error at parsing tokens", it)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Interpreter())
            }
            interpreter = interpreter.interpret(ast)
            bytesRead = stream.read(buffer)
        }
        logger.info("Snippet executed successfully")
        stream.close()
        return ResponseEntity.ok(interpreter)
    }

    override fun format(version: String, file: MultipartFile, config: MultipartFile): ResponseEntity<String> {
        logger.info("Formatting Code")
        val lexer = LexerFactoryImpl(version).create()
        val reader = ConfigurationReaderProvider().getReader(getExtension(config)).getOrElse {
            logger.error("Error at creating reader for formatter: ", it)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it.message)
        }
        val tempConfigFile = createConfigTempFile(config)
        val configurations =
            reader.readFileAndBuildRules(tempConfigFile).getOrElse {
                logger.error("Error at building configurations for formatter: ", it)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(it.message)
            }
        val code = file.inputStream.bufferedReader().readText();
        val formatter = ASTFormatter(lexer, MyParser(), configurations)
        val formattedCode = formatter.formatString(code)

        tempConfigFile.delete()
        logger.info("Code formatted successfully")
        return ResponseEntity.ok(formattedCode)
    }



    private fun getExtension(file: MultipartFile): String {
        return file.originalFilename?.substringAfterLast(".") ?: ""
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


    fun lexAndParse(file: MultipartFile, version: String): Result<Scope> {
        val lexer = LexerFactoryImpl(version).create()
        val parser = MyParser()
        val text = file.inputStream.bufferedReader().readText()
        val tokens = lexer.tokenize(text)
        val ast = parser.parseTokens(tokens).getOrElse {
            return Result.failure(it)
        }
        return Result.success(ast)
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
