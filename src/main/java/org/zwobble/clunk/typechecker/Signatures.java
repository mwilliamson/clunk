package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.*;

public class Signatures {
    private Signatures() {
    }

    public static Signature toSignature(Type type, TypeCheckerContext context) {
        // TODO: handle not callable
        if (type instanceof StaticFunctionType staticFunctionType) {
            return new SignatureStaticFunction(staticFunctionType);
        } else if (type instanceof MethodType methodType) {
            if (methodType.typeLevelParams().isEmpty()) {
                return new SignatureNonGenericMethod(methodType);
            } else {
                return new SignatureGenericMethod(methodType);
            }
        } else if (type instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value() instanceof RecordType recordType) {
            var positionalParams = context.fieldsOf(recordType).stream()
                .map(field -> (Type)field.type().value())
                .toList();
            return new SignatureConstructorRecord(positionalParams, recordType);
        } else if (type instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value().equals(Types.STRING_BUILDER)) {
            return new SignatureConstructorStringBuilder();
        } else {
            throw new UnsupportedOperationException("Not callable");
        }
    }
}
