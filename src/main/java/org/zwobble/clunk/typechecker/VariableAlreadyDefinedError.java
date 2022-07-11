package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class VariableAlreadyDefinedError extends SourceError {
    private final String variableName;

    public VariableAlreadyDefinedError(String variableName, Source source) {
        super("A variable named " + variableName + " has already been defined", source);
        this.variableName = variableName;
    }

    public String variableName() {
        return variableName;
    }
}
