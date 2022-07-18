package org.zwobble.clunk.backends.python.ast;

public enum PythonPrecedence {
    // See: https://docs.python.org/3.10/reference/expressions.html
    ASSIGNMENT,
    LAMBDA,
    CONDITIONAL,
    BOOLEAN_OR,
    BOOLEAN_AND,
    BOOLEAN_NOT,
    COMPARISON,
    BITWISE_OR,
    BITWISE_XOR,
    BITWISE_AND,
    SHIFT,
    ADDITION,
    MULTIPLICATION,
    PREFIX,
    EXPONENTIATION,
    AWAIT,
    CALL,
    PRIMARY,
}
