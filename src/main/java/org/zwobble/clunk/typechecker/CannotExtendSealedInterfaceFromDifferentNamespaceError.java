package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class CannotExtendSealedInterfaceFromDifferentNamespaceError extends SourceError {
    public CannotExtendSealedInterfaceFromDifferentNamespaceError(Source source) {
        super("Cannot extend sealed interface from different namespace", source);
    }
}
