package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class NamedParamsNotInLexicographicalOrderError extends SourceError {
    public NamedParamsNotInLexicographicalOrderError(Source source) {
        super("Named params are not in lexicographical order", source);
    }
}
