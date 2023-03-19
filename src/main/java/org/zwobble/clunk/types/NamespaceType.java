package org.zwobble.clunk.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record NamespaceType(NamespaceId id, Map<String, Type> fields) implements Type {
    @Override
    public String describe() {
        return id.toString();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }

    public static Builder builder(NamespaceId id) {
        return new Builder(id, List.of());
    }

    public static record Builder(NamespaceId id, List<Map.Entry<String, Type>> fields) {
        public NamespaceType build() {
            return new NamespaceType(
                id,
                fields.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
        }

        public Builder addFunction(String functionName, List<Type> positionalParams, Type returnType) {
            var fields = new ArrayList<>(this.fields);
            var params = new ParamTypes(positionalParams, List.of());
            var type = new StaticFunctionType(id, functionName, Optional.empty(), params, returnType, Visibility.PUBLIC);
            fields.add(Map.entry(functionName, type));
            return new Builder(id, fields);
        }

        public Builder addField(String fieldName, Type fieldType) {
            var fields = new ArrayList<>(this.fields);
            fields.add(Map.entry(fieldName, fieldType));
            return new Builder(id, fields);
        }
    }
}
