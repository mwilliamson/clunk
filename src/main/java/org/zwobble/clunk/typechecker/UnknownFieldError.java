package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public class UnknownFieldError extends SourceError {
    private final Type type;
    private final String fieldName;

    public UnknownFieldError(Type type, String fieldName, Source source) {
        super("Unknown field " + fieldName + " on " + type.describe(), source);
        this.type = type;
        this.fieldName = fieldName;
    }

    public Type getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }
}
