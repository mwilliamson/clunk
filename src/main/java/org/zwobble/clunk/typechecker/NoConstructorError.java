package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public class NoConstructorError extends SourceError {
    public NoConstructorError(Type type, Source source) {
        super(type.describe() + " does not have a constructor", source);
    }
}
