package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class NamedArgsNotInLexicographicalOrderError extends SourceError {
    public NamedArgsNotInLexicographicalOrderError(Source source) {
        super("Named args are not in lexicographical order", source);
    }
}
