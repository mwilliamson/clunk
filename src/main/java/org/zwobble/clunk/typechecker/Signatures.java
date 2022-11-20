package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;

public class Signatures {
    private Signatures() {
    }

    public static Signature toSignature(Type type, TypeCheckerContext context, Source source) {
        if (type instanceof CallableType callableType) {
            if (callableType.typeLevelParams().isEmpty()) {
                return new SignatureNonGenericCallable(callableType);
            } else {
                return new SignatureGenericCallable(callableType);
            }
        } else if (type instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value() instanceof RecordType recordType) {
            if (recordType.isPrivate() && !recordType.namespaceName().equals(context.namespaceName())) {
                throw new NotVisibleError("The constructor for " + recordType.describe() + " is not visible from other namespaces", source);
            }
            return new SignatureConstructorRecord(context.constructorType(recordType));
        } else {
            throw new UnexpectedTypeError(Types.CALLABLE, type, source);
        }
    }
}
