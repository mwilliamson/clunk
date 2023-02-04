package org.zwobble.clunk.parser;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class PositionalParamAfterNamedParamError extends SourceError {
    public PositionalParamAfterNamedParamError(Source source) {
        super("Positional param cannot follow named param", source);
    }
}
