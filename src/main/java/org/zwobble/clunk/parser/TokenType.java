package org.zwobble.clunk.parser;

public enum TokenType {
    KEYWORD_AS,
    KEYWORD_CASE,
    KEYWORD_ELSE,
    KEYWORD_ENUM,
    KEYWORD_FALSE,
    KEYWORD_FOR,
    KEYWORD_FUN,
    KEYWORD_IF,
    KEYWORD_IMPORT,
    KEYWORD_IN,
    KEYWORD_INSTANCEOF,
    KEYWORD_INTERFACE,
    KEYWORD_PROPERTY,
    KEYWORD_RECORD,
    KEYWORD_RETURN,
    KEYWORD_SEALED,
    KEYWORD_SWITCH,
    KEYWORD_TEST,
    KEYWORD_TEST_SUITE,
    KEYWORD_TRUE,
    KEYWORD_VAR,
    KEYWORD_YIELD,

    SYMBOL_AMPERSAND_AMPERSAND,
    SYMBOL_ARROW,
    SYMBOL_BANG,
    SYMBOL_BAR_BAR,
    SYMBOL_BRACE_OPEN,
    SYMBOL_BRACE_CLOSE,
    SYMBOL_COLON,
    SYMBOL_COMMA,
    SYMBOL_DOT,
    SYMBOL_EQUALS,
    SYMBOL_EQUALS_EQUALS,
    SYMBOL_FORWARD_SLASH,
    SYMBOL_HASH_OPEN,
    SYMBOL_NOT_EQUAL,
    SYMBOL_PAREN_OPEN,
    SYMBOL_PAREN_CLOSE,
    SYMBOL_PLUS,
    SYMBOL_SEMICOLON,
    SYMBOL_SQUARE_OPEN,
    SYMBOL_SQUARE_CLOSE,
    SYMBOL_SUBTYPE,

    IDENTIFIER,
    INT,
    STRING,

    BLANK_LINE,
    COMMENT_SINGLE_LINE,
    WHITESPACE,
    UNKNOWN,
    END,
}
