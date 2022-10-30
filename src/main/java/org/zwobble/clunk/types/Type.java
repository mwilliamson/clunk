package org.zwobble.clunk.types;

public interface Type extends TypeLevelValue, TypeSet {
    String describe();
    String identifier();
    Type replace(TypeMap typeMap);
}
