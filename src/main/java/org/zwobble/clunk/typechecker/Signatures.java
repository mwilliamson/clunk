package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

import java.util.List;

public class Signatures {
    private Signatures() {
    }

    public static Signature toSignature(Type type, TypeCheckerContext context) {
        // TODO: handle not callable
        if (type instanceof FunctionType functionType) {
            return new Signature(functionType.positionalParams(), functionType.returnType());
        } else if (type instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value() instanceof RecordType recordType) {
            var positionalParams = context.fieldsOf(recordType).stream()
                .map(field -> (Type)field.type().value())
                .toList();
            return new Signature(positionalParams, recordType);
        } else if (type instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value().equals(Types.STRING_BUILDER)) {
            return new Signature(List.of(), Types.STRING_BUILDER);
        } else {
            throw new UnsupportedOperationException("Not callable");
        }
    }
}
