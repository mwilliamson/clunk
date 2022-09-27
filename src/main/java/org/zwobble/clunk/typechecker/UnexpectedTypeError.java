package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.TypeSet;

public class UnexpectedTypeError extends SourceError {
    private final TypeSet expected;
    private final Type actual;

    public UnexpectedTypeError(TypeSet expected, Type actual, Source source) {
        super("Expected type: " + expected.describe() + "\nbut was: " + actual.describe(), source);
        this.expected = expected;
        this.actual = actual;
    }

    public TypeSet getExpected() {
        return expected;
    }

    public Type getActual() {
        return actual;
    }
}
