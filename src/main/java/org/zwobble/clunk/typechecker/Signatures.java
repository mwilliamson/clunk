package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

import java.util.List;

public class Signatures {
    private Signatures() {
    }

    public static Signature toSignature(Type type, TypeCheckerContext context) {
        // TODO: handle not callable
        if (type instanceof StaticFunctionType staticFunctionType) {
            return new Signature(SignatureType.STATIC_METHOD, staticFunctionType.positionalParams(), staticFunctionType.returnType());
        } else if (type instanceof FunctionType functionType) {
            return new Signature(SignatureType.METHOD, functionType.positionalParams(), functionType.returnType());
        } else if (type instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value() instanceof RecordType recordType) {
            var positionalParams = context.fieldsOf(recordType).stream()
                .map(field -> (Type)field.type().value())
                .toList();
            return new Signature(SignatureType.CONSTRUCTOR, positionalParams, recordType);
        } else if (type instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value().equals(Types.STRING_BUILDER)) {
            return new Signature(SignatureType.CONSTRUCTOR, List.of(), Types.STRING_BUILDER);
        } else {
            throw new UnsupportedOperationException("Not callable");
        }
    }
}
