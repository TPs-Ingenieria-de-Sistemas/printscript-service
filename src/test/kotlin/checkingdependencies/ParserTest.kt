package checkingdependencies

import ast.LiteralArgument
import ast.Range
import ast.Scope
import ast.VariableDeclaration
import lexer.LexerImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.MyParser
import parser.Parser

class ParserTest {
    @Test
    fun testParser() {
        val code = "let a: number = 5;" // range 14, including spaces...
        val tokens = LexerImpl().tokenize(code)
        val parser: Parser = MyParser()
        val ast = parser.parseTokens(tokens)
        val expected =
            Result.success(
                Scope(
                    "program",
                    Range(0, 17),
                    listOf(
                        VariableDeclaration(Range(4, 4), "a", "number", LiteralArgument(Range(16, 16), "5", "number")),
                    ),
                ),
            )

        Assertions.assertEquals(expected, ast)
    }
}
