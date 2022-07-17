package org.zwobble.clunk.parser;

public enum TokenType {
    KEYWORD_ELSE,
    KEYWORD_ENUM,
    KEYWORD_FALSE,
    KEYWORD_FUN,
    KEYWORD_IF,
    KEYWORD_IMPORT,
    KEYWORD_INTERFACE,
    KEYWORD_PROPERTY,
    KEYWORD_RECORD,
    KEYWORD_RETURN,
    KEYWORD_SEALED,
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
    SYMBOL_FORWARD_SLASH,
    SYMBOL_PAREN_OPEN,
    SYMBOL_PAREN_CLOSE,
    SYMBOL_SEMICOLON,
    SYMBOL_SQUARE_OPEN,
    SYMBOL_SQUARE_CLOSE,
    SYMBOL_SUBTYPE,

    IDENTIFIER,
    INT,
    STRING,

    COMMENT_SINGLE_LINE,
    WHITESPACE,
    UNKNOWN,
    END,
}
