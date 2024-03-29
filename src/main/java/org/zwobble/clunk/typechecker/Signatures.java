package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;

public class Signatures {
    private Signatures() {
    }

    public static Signature toSignature(Type type, TypeCheckerContext context, Source source) {
        if (type instanceof CallableType callableType) {
            return callableToSignature(callableType, context, source);
        } else if (type instanceof TypeLevelValueType typeLevelValueType) {
            if (typeLevelValueType.value() instanceof RecordType recordType) {
                var constructorType = context.constructorType(recordType);
                if (constructorType.isEmpty()) {
                    throw new NoConstructorError(recordType, source);
                }
                return callableToSignature(constructorType.get(), context, source);
            } else if (typeLevelValueType.value() instanceof TypeConstructor typeConstructor) {
                var constructorType = context.constructorType(typeConstructor);
                if (constructorType.isEmpty()) {
                    throw new NoConstructorError(typeConstructor.genericType(), source);
                }
                return callableToSignature(constructorType.get(), context, source);
            }
        }

        throw new UnexpectedTypeError(Types.CALLABLE, type, source);
    }

    private static Signature callableToSignature(CallableType callableType, TypeCheckerContext context, Source source) {
        if (callableType.visibility().equals(Visibility.PRIVATE) && !callableType.namespaceId().equals(context.namespaceId())) {
            throw new NotVisibleError(callableType.describe() + " is not visible from other namespaces", source);
        } else if (callableType.typeLevelParams().isEmpty()) {
            return new SignatureNonGeneric(callableType);
        } else {
            return new SignatureGeneric(callableType);
        }
    }
}
