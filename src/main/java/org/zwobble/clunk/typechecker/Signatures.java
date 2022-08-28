package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.TypeLevelValueType;

public class Signatures {
    private Signatures() {
    }

    public static Signature toSignature(Type type, TypeCheckerContext context) {
        // TODO: handle not callable
        if (type instanceof StaticFunctionType functionType) {
            return new Signature(functionType.positionalParams(), functionType.returnType());
        } else if (type instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value() instanceof RecordType recordType) {
            var positionalParams = context.fieldsOf(recordType).stream()
                .map(field -> (Type)field.type().value())
                .toList();
            return new Signature(positionalParams, recordType);
        } else {
            throw new UnsupportedOperationException("Not callable");
        }
    }
}
