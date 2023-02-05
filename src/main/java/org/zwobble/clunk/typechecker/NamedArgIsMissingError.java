package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class NamedArgIsMissingError extends SourceError {
    private final String argName;

    public NamedArgIsMissingError(String argName, Source source) {
        super("Missing named arg: " + argName, source);
        this.argName = argName;
    }

    public String argName() {
        return argName;
    }
}
