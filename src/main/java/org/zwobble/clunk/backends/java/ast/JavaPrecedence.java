package org.zwobble.clunk.backends.java.ast;

public enum JavaPrecedence {
    // See: https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html
    ASSIGNMENT,
    TERNARY,
    LOGICAL_OR,
    LOGICAL_AND,
    BITWISE_INCLUDE_OR,
    BITWISE_EXCLUSIVE_OR,
    BITWISE_AND,
    EQUALITY,
    RELATIONAL,
    SHIFT,
    ADDITIVE,
    MULTIPLICATIVE,
    UNARY,
    POSTFIX,
    CALL,
    PRIMARY,
}
