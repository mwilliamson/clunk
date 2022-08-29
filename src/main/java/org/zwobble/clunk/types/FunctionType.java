package org.zwobble.clunk.types;

import java.util.List;

public interface FunctionType extends Type {
    List<Type> positionalParams();
    Type returnType();
}
