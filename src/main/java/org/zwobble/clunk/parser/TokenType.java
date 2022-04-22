package org.zwobble.clunk.parser;

public enum TokenType {
    KEYWORD_FALSE,
    KEYWORD_FUN,
    KEYWORD_IMPORT,
    KEYWORD_RECORD,
    KEYWORD_RETURN,
    KEYWORD_TEST,
    KEYWORD_TRUE,
    KEYWORD_VAR,

    SYMBOL_ARROW,
    SYMBOL_BRACE_OPEN,
    SYMBOL_BRACE_CLOSE,
    SYMBOL_COLON,
    SYMBOL_COMMA,
    SYMBOL_DOT,
    SYMBOL_EQUALS,
    SYMBOL_PAREN_OPEN,
    SYMBOL_PAREN_CLOSE,
    SYMBOL_SEMICOLON,

    IDENTIFIER,
    INT,
    STRING,

    WHITESPACE,
    UNKNOWN,
    END,
}
