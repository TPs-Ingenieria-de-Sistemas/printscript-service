package com.example.printscriptservice.printscript.service.implementations

import Linter
import MyLinter
import factory.LexerFactoryImpl
import interpreter.Interpreter
import java.io.File
import lexer.Lexer
import parser.MyParser
import parser.Parser


// None of these are handling versions.
class LanguageFactory(val language: String) {


    fun getLinter(): Linter {
        return when {
            languageIsPrintScript()-> MyLinter()
            else -> throw IllegalArgumentException("Language not supported")
        }
    }

    fun getInterpreter(): Interpreter {
        return when{
            languageIsPrintScript() -> Interpreter()
            else -> throw IllegalArgumentException("Language not supported")
        }
    }

    fun format(file: String,
               confifFile: File): File {
        when {
            languageIsPrintScript()-> {
                format(file, confifFile);
                return File(file);
            }
            else -> throw IllegalArgumentException("Language not supported")
        }
    }

    fun getParser(): Parser {
        return when {
            languageIsPrintScript() -> MyParser()
            else -> throw IllegalArgumentException("Language not supported")
        }
    }

    fun getLexer(): Lexer {
        return when {
            languageIsPrintScript() -> LexerFactoryImpl("1.1").create() // TODO: version
            else -> throw IllegalArgumentException("Language not supported")
        }
    }

    fun languageIsPrintScript(): Boolean {
        return when (language) {
            "printscript", "ps", "PrintScript", "printScript" -> true
            else -> false
        }
    }

}
