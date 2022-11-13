package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class CannotPassTypeLevelArgsToNonGenericValueError extends SourceError {
    public CannotPassTypeLevelArgsToNonGenericValueError(Source source) {
        super("Cannot pass type-level args to non-generic value", source);
    }
}
