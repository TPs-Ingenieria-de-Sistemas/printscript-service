package com.example.printscriptservice.app

import MyLinter
import com.github.ajalt.clikt.core.subcommands
import commands.*
import interpreter.Interpreter
import lexer.LexerImpl
import parser.MyParser

fun main(args: Array<String>): Unit =
    Printscript().subcommands(
        Validate(LexerImpl(), MyParser()),
        Formatting(),
        Analyzing(LexerImpl(), MyParser(), MyLinter()),
        Execute(LexerImpl(), MyParser(), Interpreter()),
    ).main(args)
