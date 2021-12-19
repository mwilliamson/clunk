package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public class UnexpectedTypeError extends SourceError {
    private final Type expected;
    private final Type actual;

    public UnexpectedTypeError(Type expected, Type actual, Source source) {
        super("Expected type: " + expected.toString() + "\nbut was: " + actual.toString(), source);
        this.expected = expected;
        this.actual = actual;
    }

    public Type getExpected() {
        return expected;
    }

    public Type getActual() {
        return actual;
    }
}
