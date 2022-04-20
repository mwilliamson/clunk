package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class CannotReturnHereError extends SourceError {
    public CannotReturnHereError(Source source) {
        super("Cannot return here", source);
    }
}
