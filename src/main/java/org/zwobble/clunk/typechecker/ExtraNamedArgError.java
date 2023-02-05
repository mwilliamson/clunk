package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class ExtraNamedArgError extends SourceError {
    private final String argName;

    public ExtraNamedArgError(String argName, Source source) {
        super("Extra named arg was passed: " + argName, source);
        this.argName = argName;
    }

    public String argName() {
        return argName;
    }
}
