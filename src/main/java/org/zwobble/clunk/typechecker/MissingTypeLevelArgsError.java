package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class MissingTypeLevelArgsError extends SourceError {
    public MissingTypeLevelArgsError(Source source) {
        super("Missing type-level args", source);
    }
}
