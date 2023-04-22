package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.backends.typescript.codegenerator.macros.*;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeScriptMacros {
    private TypeScriptMacros() {
    }

    private static final Map<Type, TypeScriptClassMacro> CLASS_MACROS = Stream.of(
        TypeScriptMutableListMacro.INSTANCE,
        TypeScriptListMacro.INSTANCE,
        TypeScriptMapMacro.INSTANCE,
        TypeScriptStringBuilderMacro.INSTANCE,
        TypeScriptUnitMacro.INSTANCE
    ).collect(Collectors.toMap(x -> x.receiverType(), x -> x));

    public static Optional<TypeScriptExpressionNode> compileTypeReference(Type type) {
        var macro = CLASS_MACROS.get(type);

        if (macro == null) {
            return Optional.empty();
        } else {
            return Optional.of(macro.compileTypeReference());
        }
    }

    public static Optional<TypeScriptExpressionNode> compileTypeConstructorReference(TypeConstructor typeConstructor) {
        var macro = CLASS_MACROS.get(typeConstructor.genericType());

        if (macro == null) {
            return Optional.empty();
        } else {
            return Optional.of(macro.compileTypeConstructorReference());
        }
    }

    public static Optional<TypeScriptClassMacro> lookupClassMacro(Type type) {
        if (type instanceof ConstructedType constructedType) {
            type = constructedType.constructor().genericType();
        }

        return Optional.ofNullable(CLASS_MACROS.get(type));
    }

    private static final Map<NamespaceId, Map<String, TypeScriptStaticFunctionMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            Types.BUILTIN_NAMESPACE_ID,
            Map.ofEntries(
                Map.entry("none", new TypeScriptStaticFunctionMacro() {
                    @Override
                    public TypeScriptExpressionNode compileCall(
                        List<TypeScriptExpressionNode> args,
                        TypeScriptCodeGeneratorContext context
                    ) {
                        return new TypeScriptNullLiteralNode();
                    }
                })
            )
        ),
        Map.entry(
            NamespaceId.source("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new TypeScriptStaticFunctionMacro() {
                    @Override
                    public TypeScriptExpressionNode compileCall(
                        List<TypeScriptExpressionNode> args,
                        TypeScriptCodeGeneratorContext context
                    ) {
                        context.addImport("@mwilliamson/precisely", "assertThat");
                        return new TypeScriptCallNode(new TypeScriptReferenceNode("assertThat"), args);
                    }
                })
            )
        ),
        Map.entry(
            NamespaceId.source("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new TypeScriptStaticFunctionMacro() {
                    @Override
                    public TypeScriptExpressionNode compileCall(
                        List<TypeScriptExpressionNode> args,
                        TypeScriptCodeGeneratorContext context
                    ) {
                        context.addImport("@mwilliamson/precisely", "deepEqualTo");
                        return new TypeScriptCallNode(new TypeScriptReferenceNode("deepEqualTo"), args);
                    }
                }),
                Map.entry("hasMember", new TypeScriptStaticFunctionMacro() {
                    @Override
                    public TypeScriptExpressionNode compileCall(
                        List<TypeScriptExpressionNode> args,
                        TypeScriptCodeGeneratorContext context
                    ) {
                        context.addImport("@mwilliamson/precisely", "hasProperties");
                        var memberName = (TypeScriptStringLiteralNode) args.get(0);
                        var memberMatcher = args.get(1);
                        return new TypeScriptCallNode(
                            new TypeScriptReferenceNode("hasProperties"),
                            List.of(new TypeScriptObjectLiteralNode(List.of(
                                new TypeScriptPropertyLiteralNode(
                                    memberName.value(),
                                    memberMatcher
                                )
                            )))
                        );
                    }
                }),
                Map.entry("isSome", new TypeScriptStaticFunctionMacro() {
                    @Override
                    public TypeScriptExpressionNode compileCall(
                        List<TypeScriptExpressionNode> args,
                        TypeScriptCodeGeneratorContext context
                    ) {
                        return args.get(0);
                    }
                })
            )
        )
    );

    public static Optional<TypeScriptStaticFunctionMacro> lookupStaticFunctionMacro(Type type) {
        if (type instanceof StaticFunctionType staticFunctionType) {
            var macro = STATIC_FUNCTION_MACROS.getOrDefault(staticFunctionType.namespaceId(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }
}
