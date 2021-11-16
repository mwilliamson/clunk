package org.zwobble.clunk.tokeniser;

public class UnexpectedTokenException extends RuntimeException {
    public UnexpectedTokenException(
        String expected,
        String actual
    ) {
        super("Expected: " + expected + "\nBut got: " + actual);
    }
}
