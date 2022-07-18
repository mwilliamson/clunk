package org.zwobble.clunk.backends.typescript.ast;

public enum TypeScriptPrecedence {
    ASSIGNMENT,
    LOGICAL_OR,
    LOGICAL_AND,
    BITWISE_OR,
    BITWISE_XOR,
    BITWISE_AND,
    EQUALITY,
    LESS_THAN,
    SHIFT,
    ADDITION,
    MULTIPLICATION,
    EXPONENTIATION,
    PREFIX,
    POSTFIX,
    CALL,
    PRIMARY,
}
