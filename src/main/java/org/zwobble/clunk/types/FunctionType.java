package org.zwobble.clunk.types;

import java.util.List;

public record FunctionType(List<Type> positionalParams, Type returnType) implements Type {
}
