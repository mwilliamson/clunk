package org.zwobble.clunk.types;

import java.util.List;

public interface TypeConstructor extends TypeLevelValue {
    List<TypeParameter> params();
}
