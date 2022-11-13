package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class WrongNumberOfTypeLevelArgsError extends SourceError {
    private final int expected;
    private final int actual;

    public WrongNumberOfTypeLevelArgsError(int expected, int actual, Source source) {
        super("Expected " + expected + " type-level arguments, but got " + actual, source);
        this.expected = expected;
        this.actual = actual;
    }

    public int getExpected() {
        return expected;
    }

    public int getActual() {
        return actual;
    }
}
