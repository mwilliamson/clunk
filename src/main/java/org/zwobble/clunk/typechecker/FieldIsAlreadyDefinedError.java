package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class FieldIsAlreadyDefinedError extends SourceError {
    private final String fieldName;

    public FieldIsAlreadyDefinedError(String fieldName, Source source) {
        super(fieldName + "is already define", source);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
