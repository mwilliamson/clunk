package org.zwobble.clunk.types;

public interface Type extends TypeLevelValue, TypeSet {
    String describe();
    Type replace(TypeMap typeMap);
}
