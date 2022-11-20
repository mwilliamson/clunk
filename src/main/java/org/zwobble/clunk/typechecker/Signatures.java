package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;

public class Signatures {
    private Signatures() {
    }

    public static Signature toSignature(Type type, TypeCheckerContext context, Source source) {
        if (type instanceof StaticFunctionType staticFunctionType) {
            return new SignatureStaticFunction(staticFunctionType);
        } else if (type instanceof MethodType methodType) {
            if (methodType.typeLevelParams().isEmpty()) {
                return new SignatureNonGenericMethod(methodType);
            } else {
                return new SignatureGenericMethod(methodType);
            }
        } else if (type instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value() instanceof RecordType recordType) {
            if (recordType.isPrivate() && !recordType.namespaceName().equals(context.namespaceName())) {
                throw new NotVisibleError("The constructor for " + recordType.describe() + " is not visible from other namespaces", source);
            }
            var positionalParams = context.fieldsOf(recordType).stream()
                .map(field -> (Type)field.type().value())
                .toList();
            return new SignatureConstructorRecord(positionalParams, recordType);
        } else {
            throw new UnexpectedTypeError(Types.CALLABLE, type, source);
        }
    }
}
