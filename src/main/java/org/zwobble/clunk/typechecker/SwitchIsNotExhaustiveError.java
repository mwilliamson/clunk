package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.RecordType;

import java.util.List;

public class SwitchIsNotExhaustiveError extends SourceError {
    private final List<RecordType> unhandledTypes;

    public SwitchIsNotExhaustiveError(List<RecordType> unhandledTypes, Source source) {
        super(
            "switch is not exhaustive: the following types are not handled:" +
                String.join("", unhandledTypes.stream().map(type -> "\n * " + type.describe()).toList()),
            source
        );
        this.unhandledTypes = unhandledTypes;
    }

    public List<RecordType> getUnhandledTypes() {
        return unhandledTypes;
    }
}
