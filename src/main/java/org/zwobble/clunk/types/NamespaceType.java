package org.zwobble.clunk.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record NamespaceType(NamespaceName name, Map<String, Type> fields) implements Type {
    @Override
    public String describe() {
        return name.toString();
    }

    public static Builder builder(NamespaceName name) {
        return new Builder(name, List.of());
    }

    public static record Builder(NamespaceName name, List<Map.Entry<String, Type>> fields) {
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

        public Builder addField(String fieldName, Type fieldType) {
            var fields = new ArrayList<>(this.fields);
            fields.add(Map.entry(fieldName, fieldType));
            return new Builder(name, fields);
        }
    }
}
