package org.zwobble.clunk.parser;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class UnrecognisedEscapeSequenceError extends SourceError {
    public UnrecognisedEscapeSequenceError(String escapeSequence, Source source) {
        super("Unrecognised escape sequence: " + escapeSequence, source);
    }
}
