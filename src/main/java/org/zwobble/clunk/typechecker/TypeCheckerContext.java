package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedRecordFieldNode;
import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public record TypeCheckerContext(
    List<StackFrame> stack,
    Map<NamespaceName, NamespaceType> namespaceTypes,
    Map<RecordType, Function<TypeCheckerContext, List<TypedRecordFieldNode>>> typeToFields,
    List<SubtypeRelation> subtypeRelations
) {
    public static final TypeCheckerContext EMPTY = new TypeCheckerContext(
        List.of(),
        Map.of(),
        Map.of(),
        List.of()
    );

    public static TypeCheckerContext stub() {
        return new TypeCheckerContext(
            List.of(StackFrame.namespace(NamespaceName.fromParts(), Builtins.ENVIRONMENT)),
            Map.of(),
            Map.of(),
            List.of()
        );
    }

    public TypeCheckerContext enterNamespace(NamespaceName namespaceName) {
        return enter(StackFrame.namespace(namespaceName));
    }

    public TypeCheckerContext enterFunction(Type returnType) {
        return enter(StackFrame.function(returnType));
    }

    public TypeCheckerContext enterTest() {
        return enter(StackFrame.test());
    }

    private TypeCheckerContext enter(StackFrame stackFrame) {
        return new TypeCheckerContext(
            Lists.concatOne(stack, stackFrame),
            namespaceTypes,
            typeToFields,
            subtypeRelations
        );
    }

    public StackFrame currentFrame() {
        return Lists.last(stack);
    }

    public Optional<Type> returnType() {
        return currentFrame().returnType();
    }

    public TypeCheckerContext updateNamespaceType(NamespaceType namespaceType) {
        var namespaceTypes = new HashMap<>(this.namespaceTypes);
        namespaceTypes.put(namespaceType.name(), namespaceType);
        return new TypeCheckerContext(stack, namespaceTypes, typeToFields, subtypeRelations);
    }

    public Optional<NamespaceType> typeOfNamespace(NamespaceName name) {
        return Optional.ofNullable(namespaceTypes.get(name));
    }

    public TypeCheckerContext withBuiltins(Map<String, Type> environment) {
        return enter(StackFrame.builtins(environment));
    }

    public TypeCheckerContext updateType(String name, Type type) {
        return new TypeCheckerContext(
            Lists.updateLast(stack, frame -> frame.updateType(name, type)),
            namespaceTypes,
            typeToFields,
            subtypeRelations
        );
    }

    public Type typeOf(String name, Source source) {
        for (var i = stack.size() - 1; i >= 0; i--) {
            var callFrame = stack.get(i);

            var type = callFrame.environment().get(name);
            if (type != null) {
                return type;
            }
        }
        throw new SourceError("unknown variable: " + name, source);
    }

    public TypeCheckerContext addFields(RecordType type, Function<TypeCheckerContext, List<TypedRecordFieldNode>> fields) {
        var typeToFields = new HashMap<>(this.typeToFields);
        typeToFields.put(type, fields);
        return new TypeCheckerContext(stack, namespaceTypes, typeToFields, subtypeRelations);
    }

    public TypeCheckerContext addSubtypeRelation(RecordType subtype, Type superType) {
        var subtypeRelations = new ArrayList<>(this.subtypeRelations);
        subtypeRelations.add(new SubtypeRelation(subtype, superType));
        return new TypeCheckerContext(stack, namespaceTypes, typeToFields, subtypeRelations);
    }

    public FieldsLookup fieldsLookup() {
        return new FieldsLookup(typeToFields.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().apply(this)
            )));
    }
}
