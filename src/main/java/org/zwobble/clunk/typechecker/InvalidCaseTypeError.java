package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public class InvalidCaseTypeError extends SourceError {
    private final Type expressionType;
    private final Type caseType;

    public InvalidCaseTypeError(Type expressionType, Type caseType, Source source) {
        super("Invalid case type " + caseType.describe() + " for expression of type " + expressionType, source);
        this.expressionType = expressionType;
        this.caseType = caseType;
    }

    public Type getExpressionType() {
        return expressionType;
    }

    public Type getCaseType() {
        return caseType;
    }
}
