package org.zwobble.clunk.types;

import java.util.List;

public record FunctionType(List<Type> positionalArgs, Type returnType) implements Type {
}
