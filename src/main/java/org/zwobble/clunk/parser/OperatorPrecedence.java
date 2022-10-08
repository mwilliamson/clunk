package org.zwobble.clunk.parser;

public enum OperatorPrecedence {
    LOGICAL_OR,
    LOGICAL_AND,
    EQUALITY,
    RELATIONAL,
    ADDITION,
    CAST,
    PREFIX,
    CALL
}
