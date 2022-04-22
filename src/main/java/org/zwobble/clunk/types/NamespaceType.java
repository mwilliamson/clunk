package org.zwobble.clunk.types;

import java.util.List;
import java.util.Map;

public record NamespaceType(List<String> name, Map<String, FunctionType> functions) implements Type {
}
