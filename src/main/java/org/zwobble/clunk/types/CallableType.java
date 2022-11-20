package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;

public interface CallableType extends Type {
    Optional<List<TypeParameter>> typeLevelParams();
    List<Type> positionalParams();
    Type returnType();
}
