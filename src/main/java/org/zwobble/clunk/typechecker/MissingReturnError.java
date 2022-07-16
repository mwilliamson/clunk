package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class MissingReturnError extends SourceError {
    public MissingReturnError(Source source) {
        super("Missing return", source);
    }
}
