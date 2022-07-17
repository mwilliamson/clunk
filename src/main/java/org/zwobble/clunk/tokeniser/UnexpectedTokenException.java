package org.zwobble.clunk.tokeniser;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class UnexpectedTokenException extends SourceError {
    private final String expected;
    private final String actual;

    public UnexpectedTokenException(
        String expected,
        String actual,
        Source source
    ) {
        super("Expected: " + expected + "\nBut got: " + actual, source);
        this.expected = expected;
        this.actual = actual;
    }

    public String getExpected() {
        return expected;
    }

    public String getActual() {
        return actual;
    }
}
