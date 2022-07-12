package org.zwobble.clunk.typechecker;

import org.pcollections.PMap;
import org.pcollections.PStack;
import org.pcollections.PVector;
import org.zwobble.clunk.ast.typed.TypedRecordFieldNode;
import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.P;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public record TypeCheckerContext(
    PStack<StackFrame> stack,
    PMap<NamespaceName, NamespaceType> namespaceTypes,
    PMap<RecordType, Function<TypeCheckerContext, List<TypedRecordFieldNode>>> typeToFields,
    PVector<SubtypeRelation> subtypeRelations
) {
    public static final TypeCheckerContext EMPTY = new TypeCheckerContext(
        P.stack(),
        P.map(),
        P.map(),
        P.vector()
    );

    public static TypeCheckerContext stub() {
        return new TypeCheckerContext(
            P.stack(StackFrame.namespace(NamespaceName.fromParts(), Builtins.ENVIRONMENT)),
            P.map(),
            P.map(),
            P.vector()
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
            stack.plus(stackFrame),
            namespaceTypes,
            typeToFields,
            subtypeRelations
        );
    }

    public StackFrame currentFrame() {
        return stack.get(0);
    }

    public Optional<Type> returnType() {
        return currentFrame().returnType();
    }

    public TypeCheckerContext updateNamespaceType(NamespaceType namespaceType) {
        var namespaceTypes = this.namespaceTypes.plus(namespaceType.name(), namespaceType);
        return new TypeCheckerContext(stack, namespaceTypes, typeToFields, subtypeRelations);
    }

    public Optional<NamespaceType> typeOfNamespace(NamespaceName name) {
        return Optional.ofNullable(namespaceTypes.get(name));
    }

    public TypeCheckerContext withBuiltins(Map<String, Type> environment) {
        return enter(StackFrame.builtins(environment));
    }

    public TypeCheckerContext updateType(String name, Type type, Source source) {
        return new TypeCheckerContext(
            P.stackUpdateTop(stack, frame -> frame.updateType(name, type, source)),
            namespaceTypes,
            typeToFields,
            subtypeRelations
        );
    }

    public Type typeOf(String name, Source source) {
        for (var callFrame : stack) {
            var type = callFrame.environment().get(name);
            if (type != null) {
                return type;
            }
        }
        throw new SourceError("unknown variable: " + name, source);
    }

    public TypeCheckerContext addFields(RecordType type, Function<TypeCheckerContext, List<TypedRecordFieldNode>> fields) {
        var typeToFields = this.typeToFields.plus(type, fields);
        return new TypeCheckerContext(stack, namespaceTypes, typeToFields, subtypeRelations);
    }

    public TypeCheckerContext addSubtypeRelation(RecordType subtype, Type superType) {
        var subtypeRelations = this.subtypeRelations.plus(new SubtypeRelation(subtype, superType));
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
