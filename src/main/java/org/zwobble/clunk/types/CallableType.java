package org.zwobble.clunk.types;

import java.util.List;

public interface CallableType extends Type {
    List<Type> positionalParams();
    Type returnType();
}
