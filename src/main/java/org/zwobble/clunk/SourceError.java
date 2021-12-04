package org.zwobble.clunk;

import org.zwobble.clunk.sources.Source;

public class SourceError extends RuntimeException {
    private final Source source;

    public SourceError(String message, Source source) {
        super(message);
        this.source = source;
    }

    public Source getSource() {
        return source;
    }
}
