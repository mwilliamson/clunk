package org.zwobble.clunk.parser;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class PositionalArgAfterNamedArgError extends SourceError {
    public PositionalArgAfterNamedArgError(Source source) {
        super("Positional arg cannot follow named arg", source);
    }
}
