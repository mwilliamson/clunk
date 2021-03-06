package org.zwobble.clunk.typechecker;

import org.pcollections.PMap;
import org.pcollections.PStack;
import org.pcollections.PVector;
import org.zwobble.clunk.ast.typed.TypedRecordFieldNode;
import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;
import org.zwobble.clunk.util.P;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record TypeCheckerContext(
    PStack<StackFrame> stack,
    PMap<NamespaceName, NamespaceType> namespaceTypes,
    PMap<RecordType, List<TypedRecordFieldNode>> typeToFields,
    PMap<RecordType, Map<String, Type>> memberTypes,
    PVector<SubtypeRelation> subtypeRelations
) {
    public static final TypeCheckerContext EMPTY = new TypeCheckerContext(
        P.stack(),
        P.map(),
        P.map(),
        P.map(),
        P.vector()
    );

    public static TypeCheckerContext stub() {
        return new TypeCheckerContext(
            P.stack(StackFrame.namespace(NamespaceName.fromParts(), Builtins.ENVIRONMENT)),
            P.map(),
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

    public TypeCheckerContext enterRecordBody(Map<String, Type> fieldTypes) {
        var fieldVariables = fieldTypes.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> Variable.member(entry.getValue())
            ));
        return enter(StackFrame.body(fieldVariables));
    }

    public TypeCheckerContext enterTest() {
        return enter(StackFrame.test());
    }

    private TypeCheckerContext enter(StackFrame stackFrame) {
        return new TypeCheckerContext(
            stack.plus(stackFrame),
            namespaceTypes,
            typeToFields,
            memberTypes,
            subtypeRelations
        );
    }

    public TypeCheckerContext leave() {
        return new TypeCheckerContext(
            stack.minus(0),
            namespaceTypes,
            typeToFields,
            memberTypes,
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
        return new TypeCheckerContext(stack, namespaceTypes, typeToFields, memberTypes, subtypeRelations);
    }

    public Optional<NamespaceType> typeOfNamespace(NamespaceName name) {
        return Optional.ofNullable(namespaceTypes.get(name));
    }

    public TypeCheckerContext withBuiltins(Map<String, Variable> environment) {
        return enter(StackFrame.builtins(environment));
    }

    public TypeCheckerContext addLocal(String name, Type type, Source source) {
        return addVariable(name, Variable.local(type), source);
    }

    private TypeCheckerContext addVariable(String name, Variable variable, Source source) {
        return new TypeCheckerContext(
            P.stackUpdateTop(stack, frame -> frame.addVariable(name, variable, source)),
            namespaceTypes,
            typeToFields,
            memberTypes,
            subtypeRelations
        );
    }

    public Variable lookup(String name, Source source) {
        for (var callFrame : stack) {
            var variable = callFrame.environment().get(name);
            if (variable != null) {
                return variable;
            }
        }
        throw new SourceError("unknown variable: " + name, source);
    }

    public Type typeOf(String name, Source source) {
        return lookup(name, source).type();
    }

    public TypeCheckerContext addFields(RecordType type, List<TypedRecordFieldNode> fields) {
        var typeToFields = this.typeToFields.plus(type, fields);
        return new TypeCheckerContext(stack, namespaceTypes, typeToFields, memberTypes, subtypeRelations);
    }

    public TypeCheckerContext addMemberTypes(RecordType type, Map<String, Type> memberTypes) {
        return new TypeCheckerContext(
            stack,
            namespaceTypes,
            typeToFields,
            this.memberTypes.plus(type, memberTypes),
            subtypeRelations
        );
    }

    public TypeCheckerContext addSubtypeRelation(RecordType subtype, InterfaceType superType) {
        var subtypeRelations = this.subtypeRelations.plus(new SubtypeRelation(subtype, superType));
        return new TypeCheckerContext(stack, namespaceTypes, typeToFields, memberTypes, subtypeRelations);
    }

    public List<TypedRecordFieldNode> fieldsOf(RecordType type) {
        return typeToFields.get(type);
    }

    public Optional<Type> memberType(RecordType type, String memberName) {
        var typeMembers = memberTypes.get(type);
        if (typeMembers == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(typeMembers.get(memberName));
    }
}
