package org.zwobble.clunk.types;

import java.util.HashMap;

public interface Type extends TypeLevelValue, TypeSet {
    String describe();
    Type replace(HashMap<TypeParameter, Type> typeMap);
}
