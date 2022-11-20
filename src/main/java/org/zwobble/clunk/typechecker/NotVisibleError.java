package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class NotVisibleError extends SourceError {
    public NotVisibleError(String message, Source source) {
        super(message, source);
    }
}
