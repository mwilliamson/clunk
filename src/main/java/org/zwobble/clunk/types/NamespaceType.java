package org.zwobble.clunk.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record NamespaceType(List<String> name, Map<String, StaticFunctionType> functions) implements Type {
    @Override
    public String describe() {
        return String.join(".", name);
    }

    public static Builder builder(List<String> name) {
        return new Builder(name, List.of());
    }

    public static record Builder(List<String> name, List<StaticFunctionType> functions) {
        public NamespaceType build() {
            return new NamespaceType(
                name,
                functions.stream().collect(Collectors.toMap(StaticFunctionType::functionName, t -> t))
            );
        }

        public Builder addFunction(String functionName, List<Type> positionalArgs, Type returnType) {
            var functions = new ArrayList<>(this.functions);
            functions.add(new StaticFunctionType(name, functionName, positionalArgs, returnType));
            return new Builder(name, functions);
        }
    }
}
