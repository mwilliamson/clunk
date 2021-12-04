package org.zwobble.clunk.parser;

public enum TokenType {
    KEYWORD_RECORD,

    SYMBOL_COLON,
    SYMBOL_COMMA,
    SYMBOL_PAREN_OPEN,
    SYMBOL_PAREN_CLOSE,

    IDENTIFIER,
    STRING,

    WHITESPACE,
    UNKNOWN,
    END,
}
