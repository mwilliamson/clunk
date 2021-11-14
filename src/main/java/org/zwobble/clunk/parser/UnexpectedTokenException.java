package org.zwobble.clunk.parser;

public class UnexpectedTokenException extends RuntimeException {
    public UnexpectedTokenException(
        String expected,
        String actual
    ) {
        super("Expected: " + expected + "\nBut got: " + actual);
    }
}
