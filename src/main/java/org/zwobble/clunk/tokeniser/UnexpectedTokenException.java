package org.zwobble.clunk.tokeniser;

import org.zwobble.clunk.SourceError;
import org.zwobble.clunk.sources.Source;

public class UnexpectedTokenException extends SourceError {
    public UnexpectedTokenException(
        String expected,
        String actual,
        Source source
    ) {
        super("Expected: " + expected + "\nBut got: " + actual, source);
    }
}
