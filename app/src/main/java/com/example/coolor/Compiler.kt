package com.example.coolor

import java.util.Locale

// --- 1. Custom Exceptions for Robust Error Handling ---

/**
 * Thrown when the Lexer encounters an unrecognized token or symbol.
 */
class LexerException(message: String) : Exception(message)

/**
 * Thrown when the Parser encounters a sequence of tokens that violates the grammar rules.
 */
class ParserException(message: String) : Exception(message)


// --- 2. Constants and Data Structures ---

/** All valid keywords in the command language. */
private val KEYWORDS: Set<String> = setOf("MAKE", "GENERATE", "PALETTE", "COLOR", "COLORS", "FROM", "TO", ":")
/** All valid base colors. */
private val COLORS: Set<String> = setOf("RED", "GREEN", "BLUE", "YELLOW", "BLACK", "BROWN", "ORANGE")
/** All valid color adjectives. */
private val ADJECTIVES: Set<String> = setOf("LIGHT", "DARK")

/** Represents a single token (word, number, or symbol) from the input stream. */
data class Token(val type: String, val value: String)

/** The structured result returned by a successful parse operation. */
data class PaletteCommand(
    val command: String,          // e.g., "GENERATE PALETTE"
    val numberOfColors: Int,      // Number of colors in the palette
)


// --- 3. Lexical Analyzer (Lexer) ---

/**
 * Breaks the raw input string into a list of Tokens.
 * This is case-insensitive, matching the original Python logic.
 */
class Lexer(private val input: String) {

    // Regex to split the input into tokens: words/numbers (\w+) or single symbols ([^\w\s]).
    private val tokenRegex = Regex("""\w+|[^\w\s]""")

    fun lexical(): List<Token> {
        // Find all matches, convert to list of strings, and filter out empty strings
        val tokens = tokenRegex.findAll(input)
            .map { it.value }
            .filter { it.isNotBlank() }

        val tokenList = mutableListOf<Token>()

        for (item in tokens) {
            val word = item.uppercase(Locale.getDefault())

            when {
                KEYWORDS.contains(word) -> {
                    tokenList.add(Token("keyword", item))
                }
                COLORS.contains(word) -> {
                    tokenList.add(Token("color", item))
                }
                ADJECTIVES.contains(word) -> {
                    tokenList.add(Token("adj", item))
                }
                word.matches(Regex("""\d+""")) -> {
                    tokenList.add(Token("number", item))
                }
                item == "(" || item == ")" || item == ":" -> {
                    // Valid symbols
                    tokenList.add(Token("symbol", item))
                }
                else -> {
                    // Unrecognized token - throw exception
                    throw LexerException("\"$item\" is not defined in the command language.")
                }
            }
        }
        return tokenList
    }
}


// --- 4. Syntactic Analyzer (Parser) ---

/**
 * Takes a list of Tokens and attempts to match them against the command grammar.
 * Grammar: (GENERATE | MAKE) PALETTE N (COLOR | COLORS) FROM [ ( ] ADJ COLOR (TO | :) ADJ COLOR [ ) ]
 */
class Parser(input: String) {
    private val tokens: List<Token>
    private var counter: Int = 0
    private val size: Int

    init {
        // 1. First, run the lexer
        this.tokens = try {
            Lexer(input).lexical()
        } catch (e: LexerException) {
            // Re-throw lexer error as parser error for consistent try-catch blocks in calling code
            throw ParserException("Lexing Error: ${e.message}")
        }
        this.size = tokens.size
    }

    /** Advances the token counter to the next token. */
    private fun move() {
        if (counter < size) {
            counter++
        }
    }

    /** Checks if the current token's value matches the expected word (case-insensitive). */
    private fun expect(expectedWord: String) {
        val upperExpected = expectedWord.uppercase(Locale.getDefault())

        if (counter < size && tokens[counter].value.uppercase(Locale.getDefault()) == upperExpected) {
            move()
        } else if (counter < size) {
            throw ParserException("Syntax error: Expected '$expectedWord' but found '${tokens[counter].value}'")
        } else {
            throw ParserException("Syntax error: Expected '$expectedWord' but found end of input.")
        }
    }

    /** Checks if the current token's value matches any of the expected words (case-insensitive). */
    private fun expectAny(expectedWords: Set<String>) {
        val upperWords = expectedWords.map { it.uppercase(Locale.getDefault()) }.toSet()
        val currentTokenValue = tokens.getOrNull(counter)?.value?.uppercase(Locale.getDefault())

        if (counter < size && currentTokenValue in upperWords) {
            move()
        } else if (counter < size) {
            throw ParserException("Syntax error: Expected one of $expectedWords but found '${tokens[counter].value}'")
        } else {
            throw ParserException("Syntax error: Expected one of $expectedWords but found end of input.")
        }
    }

    /**
     * Parses the token stream and returns the structured command object.
     * @return PaletteCommand object with parsed details.
     * @throws ParserException if a syntax or semantic error is found.
     */
    fun parse(): PaletteCommand {
        if (tokens.isEmpty()) {
            throw ParserException("Input is empty.")
        }

        // 1. (GENERATE | MAKE) PALETTE
        expectAny(setOf("GENERATE", "MAKE"))
        val commandStart = tokens[0].value.uppercase(Locale.getDefault()) // "GENERATE" or "MAKE"
        expect("PALETTE")
        val command = "$commandStart PALETTE"

        // 2. N (number 2-9)
        if (counter >= size || tokens[counter].type != "number") {
            throw ParserException("Syntax error: Expected a number (2-9) found end of input or wrong token type.")
        }

        val numberOfColors = tokens[counter].value.toIntOrNull()
        if (numberOfColors == null || numberOfColors < 2 || numberOfColors > 9) {
            throw ParserException("Syntax error: Expected number from 2 to 9 but found '${tokens[counter].value}'")
        }
        move()

        // 3. (COLOR | COLORS)
        expectAny(setOf("COLOR", "COLORS"))

//        // 4. FROM
//        expect("FROM")
//
//        // 5. Optional '('
//        if (counter < size && tokens[counter].value == "(") {
//            expect("(")
//        }
//
//        // 6. Color One Adjective (LIGHT | DARK)
//        if (counter >= size || tokens[counter].type != "adj") {
//            throw ParserException("Syntax error: Expected adjective (LIGHT or DARK) found ${tokens.getOrNull(counter)?.value ?: "end of input"}")
//        }
//        val colorOneAdj = tokens[counter].value.uppercase(Locale.getDefault())
//        move()
//
//        // 7. Color One (RED | BLUE | ...)
//        if (counter >= size || tokens[counter].type != "color") {
//            throw ParserException("Syntax error: Expected color (RED, BLUE, etc.) found ${tokens.getOrNull(counter)?.value ?: "end of input"}")
//        }
//        val colorOne = tokens[counter].value.uppercase(Locale.getDefault())
//        move()
//
//        // 8. (TO | :)
//        expectAny(setOf("TO", ":"))
//
//        // 9. Color Two Adjective (LIGHT | DARK)
//        if (counter >= size || tokens[counter].type != "adj") {
//            throw ParserException("Syntax error: Expected adjective (LIGHT or DARK) found ${tokens.getOrNull(counter)?.value ?: "end of input"}")
//        }
//        val colorTwoAdj = tokens[counter].value.uppercase(Locale.getDefault())
//        move()
//
//        // 10. Color Two (RED | BLUE | ...)
//        if (counter >= size || tokens[counter].type != "color") {
//            throw ParserException("Syntax error: Expected color (RED, BLUE, etc.) found ${tokens.getOrNull(counter)?.value ?: "end of input"}")
//        }
//        val colorTwo = tokens[counter].value.uppercase(Locale.getDefault())
//        move()
//
//        // 11. Optional ')'
//        if (counter < size && tokens[counter].value == ")") {
//            expect(")")
//        }
//
//        // Check for remaining tokens
        if (counter < size) {
            throw ParserException("Syntax error: Unexpected token '${tokens[counter].value}' after valid command.")
        }

        // Successfully parsed, return the structured result
        return PaletteCommand(
            command = command,
            numberOfColors = numberOfColors

        )
    }
}


// --- 5. Example Usage (Main Function) ---

fun main() {
    val testStrings = listOf(
        "GENERATE PALETTE 5 COLORS FROM (light blue TO dark orange)",
        "Make paLette 6 COLORS FROM",
        "MAKE PALETTE 3 COLOR FROM dark RED : light green"
    )

    testStrings.forEach { input ->
        println("\n--- Input: \"$input\" ---")
        try {
            val result = Parser(input).parse()
            println("Successfully Parsed:")
            println("  Command: ${result.command}")
            println("  Number of Colors: ${result.numberOfColors}")
        } catch (e: Exception) {
            println("Parsing Failed: ${e.message}")
        }
    }

    // Example of a failing case for error testing
    val errorString = "GENERATE PALETTE 10 COLORS FROM light teal TO dark purple"
    println("\n--- Input: \"$errorString\" ---")
    try {
        Parser(errorString).parse()
    } catch (e: Exception) {
        println("Parsing Failed: ${e.message}")
    }
}