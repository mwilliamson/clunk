package org.zwobble.clunk.typechecker;

import org.pcollections.PMap;
import org.pcollections.PStack;
import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.errors.CompilerError;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;
import org.zwobble.clunk.util.Maps;
import org.zwobble.clunk.util.P;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record TypeCheckerContext(
    PStack<StackFrame> stack,
    PMap<NamespaceId, NamespaceType> namespaceTypes,
    PMap<StructuredType, ConstructorType> constructorTypes,
    PMap<Type, Map<String, Type>> memberTypes,
    SubtypeRelations subtypeRelations
) {
    public static final TypeCheckerContext EMPTY = new TypeCheckerContext(
        P.stack(),
        P.map(),
        P.map(),
        P.map(),
        SubtypeRelations.EMPTY
    );

    public static TypeCheckerContext stub() {
        return new TypeCheckerContext(
            P.stack(StackFrame.namespace(NamespaceId.source(), Builtins.ENVIRONMENT)),
            P.map(),
            P.map(),
            P.map(),
            SubtypeRelations.EMPTY
        );
    }

    public TypeCheckerContext enterNamespace(NamespaceId namespaceId) {
        return enter(StackFrame.namespace(namespaceId));
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

    public TypeCheckerContext enterTestSuite() {
        return enter(StackFrame.testSuite());
    }

    private TypeCheckerContext enter(StackFrame stackFrame) {
        return new TypeCheckerContext(
            stack.plus(stackFrame),
            namespaceTypes,
            constructorTypes,
            memberTypes,
            subtypeRelations
        );
    }

    public TypeCheckerContext leave() {
        return new TypeCheckerContext(
            stack.minus(0),
            namespaceTypes,
            constructorTypes,
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
        return new TypeCheckerContext(
            stack,
            this.namespaceTypes.plus(namespaceType.id(), namespaceType),
            constructorTypes,
            memberTypes.plus(namespaceType, namespaceType.fields()),
            subtypeRelations
        );
    }

    public Optional<NamespaceType> typeOfNamespace(NamespaceId id) {
        return Optional.ofNullable(namespaceTypes.get(id));
    }

    public TypeCheckerContext withBuiltins(Map<String, Variable> environment) {
        return enter(StackFrame.builtins(environment));
    }

    public TypeCheckerContext addLocal(String name, Type type, Source source) {
        return addVariable(name, Variable.local(type), source);
    }

    public TypeCheckerContext updateLocal(String name, Type type, Source source) {
        return updateVariable(name, Variable.local(type), source);
    }

    private TypeCheckerContext addVariable(String name, Variable variable, Source source) {
        return new TypeCheckerContext(
            P.stackUpdateTop(stack, frame -> frame.addVariable(name, variable, source)),
            namespaceTypes,
            constructorTypes,
            memberTypes,
            subtypeRelations
        );
    }

    private TypeCheckerContext updateVariable(String name, Variable variable, Source source) {
        return new TypeCheckerContext(
            P.stackUpdateTop(stack, frame -> frame.updateVariable(name, variable, source)),
            namespaceTypes,
            constructorTypes,
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

    public TypeCheckerContext addConstructorType(ConstructorType constructorType) {
        var typeToFields = this.constructorTypes.plus(constructorType.returnType(), constructorType);
        return new TypeCheckerContext(stack, namespaceTypes, typeToFields, memberTypes, subtypeRelations);
    }

    public TypeCheckerContext addMemberTypes(Type type, Map<String, Type> memberTypes) {
        return new TypeCheckerContext(
            stack,
            namespaceTypes,
            constructorTypes,
            this.memberTypes.plus(type, memberTypes),
            subtypeRelations
        );
    }

    public TypeCheckerContext addSubtypeRelation(StructuredType subtype, StructuredType superType) {
        var subtypeRelations = this.subtypeRelations.addExtendedType(subtype, superType);
        return new TypeCheckerContext(stack, namespaceTypes, constructorTypes, memberTypes, subtypeRelations);
    }

    public TypeCheckerContext addSealedInterfaceCase(InterfaceType sealedInterfaceType, RecordType caseType) {
        var subtypeRelations = this.subtypeRelations.addSealedInterfaceCase(sealedInterfaceType, caseType);
        return new TypeCheckerContext(stack, namespaceTypes, constructorTypes, memberTypes, subtypeRelations);
    }

    public Optional<ConstructorType> constructorType(RecordType type) {
        return Optional.ofNullable(constructorTypes.get(type));
    }

    public Optional<ConstructorType> constructorType(TypeConstructor typeConstructor) {
        if (typeConstructor.genericType() instanceof RecordType recordType) {
            var constructorType = constructorType(recordType);
            return constructorType.map(t -> t
                .withTypeParams(typeConstructor.params())
                .withReturnType(Types.construct(typeConstructor, typeConstructor.params()))
            );
        } else {
            return Optional.empty();
        }
    }

    public Map<String, Type> memberTypes(Type type) {
        // TODO: test this!
        if (type instanceof ConstructedType constructedType) {
            var genericType = constructedType.constructor().genericType();
            var typeMap = constructedType.typeMap();
            return Maps.mapValues(memberTypes(genericType), memberType -> memberType.replace(typeMap));
        }

        var typeMembers = memberTypes.get(type);
        if (typeMembers == null) {
            return Map.ofEntries();
        }

        return typeMembers;
    }

    public Optional<Type> memberType(Type type, String memberName) {
        if (type instanceof ConstructedType constructedType) {
            var genericType = constructedType.constructor().genericType();
            return memberType(genericType, memberName).map(memberType -> {
                return memberType.replace(constructedType.typeMap());
            });
        }

        var typeMembers = memberTypes.get(type);
        if (typeMembers == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(typeMembers.get(memberName));
    }

    public List<RecordType> sealedInterfaceCases(InterfaceType sealedInterfaceType) {
        return subtypeRelations.sealedInterfaceCases(sealedInterfaceType);
    }

    public boolean isSubType(Type subtype, Type supertype) {
        return subtypeRelations.isSubType(subtype, supertype);
    }

    public NamespaceId namespaceId() {
        for (var frame : stack) {
            var namespaceId = frame.namespaceId();
            if (namespaceId.isPresent()) {
                return namespaceId.get();
            }
        }
        // TODO: throw a better exception
        throw new CompilerError("not in a namespace");
    }
}
