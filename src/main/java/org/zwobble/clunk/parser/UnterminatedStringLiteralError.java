package org.zwobble.clunk.parser;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class UnterminatedStringLiteralError extends SourceError {
    public UnterminatedStringLiteralError(Source source) {
        super("Unterminated string literal", source);
    }
}
