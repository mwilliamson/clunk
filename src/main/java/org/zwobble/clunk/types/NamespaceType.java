package org.zwobble.clunk.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record NamespaceType(List<String> name, Map<String, Type> fields) implements Type {
    @Override
    public String describe() {
        return String.join(".", name);
    }

    public static Builder builder(List<String> name) {
        return new Builder(name, List.of());
    }

    public static record Builder(List<String> name, List<Map.Entry<String, Type>> fields) {
        public NamespaceType build() {
            return new NamespaceType(
                name,
                fields.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
        }

        public Builder addFunction(String functionName, List<Type> positionalArgs, Type returnType) {
            var fields = new ArrayList<>(this.fields);
            var type = new StaticFunctionType(name, functionName, positionalArgs, returnType);
            fields.add(Map.entry(functionName, type));
            return new Builder(name, fields);
        }
    }
}
