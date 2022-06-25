package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class CannotExtendFinalTypeError extends SourceError {
    public CannotExtendFinalTypeError(Source source) {
        super("Cannot extend final type", source);
    }
}
