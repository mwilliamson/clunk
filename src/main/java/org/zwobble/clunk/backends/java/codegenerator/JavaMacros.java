package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.backends.java.codegenerator.macros.*;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaMacros {
    private static final Map<NamespaceId, Map<String, JavaStaticFunctionMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            Types.BUILTIN_NAMESPACE_ID,
            Map.ofEntries(
                Map.entry("none", new JavaStaticFunctionMacro() {
                    @Override
                    public JavaExpressionNode compileCall(
                        List<JavaExpressionNode> args,
                        JavaCodeGeneratorContext context
                    ) {
                        context.addImportType("java.util.Optional");
                        return new JavaCallNode(
                            new JavaMemberAccessNode(
                                new JavaReferenceNode("Optional"),
                                "empty"
                            ),
                            args
                        );
                    }
                })
            )
        ),
        Map.entry(
            NamespaceId.source("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new JavaStaticFunctionMacro() {
                    @Override
                    public JavaExpressionNode compileCall(
                        List<JavaExpressionNode> args,
                        JavaCodeGeneratorContext context
                    ) {
                        context.addImportStatic("org.zwobble.precisely.AssertThat", "assertThat");
                        return new JavaCallNode(
                            new JavaReferenceNode("assertThat"),
                            args
                        );
                    }
                })
            )
        ),
        Map.entry(
            NamespaceId.source("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new JavaStaticFunctionMacro() {
                    @Override
                    public JavaExpressionNode compileCall(
                        List<JavaExpressionNode> args,
                        JavaCodeGeneratorContext context
                    ) {
                        context.addImportStatic("org.zwobble.precisely.Matchers", "equalTo");
                        return new JavaCallNode(
                            new JavaReferenceNode("equalTo"),
                            args
                        );
                    }
                }),
                Map.entry("has", new JavaStaticFunctionMacro() {
                    @Override
                    public JavaExpressionNode compileCall(
                        List<JavaExpressionNode> args,
                        JavaCodeGeneratorContext context
                    ) {
                        context.addImportStatic("org.zwobble.precisely.Matchers", "has");
                        var memberDefinitionReference = (JavaMethodReferenceStaticNode) args.get(0);
                        var memberMatcher = args.get(1);
                        return new JavaCallNode(
                            new JavaReferenceNode("has"),
                            List.of(
                                new JavaStringLiteralNode(memberDefinitionReference.methodName()),
                                memberDefinitionReference,
                                memberMatcher
                            )
                        );
                    }
                }),
                Map.entry("isSome", new JavaStaticFunctionMacro() {
                    @Override
                    public JavaExpressionNode compileCall(
                        List<JavaExpressionNode> args,
                        JavaCodeGeneratorContext context
                    ) {
                        context.addImportStatic("org.zwobble.precisely.Matchers", "isOptionalOf");
                        return new JavaCallNode(
                            new JavaReferenceNode("isOptionalOf"),
                            args
                        );
                    }
                })
            )
        )
    );

    private static final Map<Type, JavaClassMacro> CLASS_MACROS = Stream.of(
        JavaListMacro.INSTANCE,
        JavaMapMacro.INSTANCE,
        JavaMutableListMacro.INSTANCE,
        JavaStringBuilderMacro.INSTANCE,
        JavaUnitMacro.INSTANCE
    ).collect(Collectors.toMap(x -> x.receiverType(), x -> x));

    public static Optional<JavaStaticFunctionMacro> lookupStaticFunctionMacro(Type receiverType) {
        if (receiverType instanceof StaticFunctionType staticFunctionType) {
            var macro = STATIC_FUNCTION_MACROS.getOrDefault(staticFunctionType.namespaceId(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<JavaExpressionNode> compileConstructorCall(
        Type type,
        List<JavaExpressionNode> positionalArgs,
        JavaCodeGeneratorContext context
    ) {
        var classMacro = lookupClassMacro(type);
        if (classMacro.isPresent()) {
            Optional<List<JavaTypeExpressionNode>> typeArgs = type instanceof ConstructedType constructedType
                ? Optional.of(
                    constructedType.args().stream()
                        .map(typeArg -> JavaCodeGenerator.typeLevelValueToTypeExpression(typeArg, true, context))
                        .toList()
                )
                : Optional.empty();
            return Optional.of(classMacro.get().compileConstructorCall(typeArgs, positionalArgs));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<JavaExpressionNode> compileMethodCall(
        Type type,
        JavaExpressionNode receiver,
        String methodName,
        List<JavaExpressionNode> positionalArgs
    ) {
        var classMacro = lookupClassMacro(type);
        if (classMacro.isPresent()) {
            return Optional.of(classMacro.get().compileMethodCall(receiver, methodName, positionalArgs));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<JavaTypeExpressionNode> compileTypeReference(Type type) {
        var classMacro = CLASS_MACROS.get(type);
        if (classMacro == null) {
            return Optional.empty();
        } else {
            return Optional.of(classMacro.compileTypeReference());
        }
    }

    public static Optional<JavaTypeExpressionNode> compileTypeConstructorReference(TypeConstructor typeConstructor) {
        var classMacro = CLASS_MACROS.get(typeConstructor.genericType());
        if (classMacro == null) {
            return Optional.empty();
        } else {
            return Optional.of(classMacro.compileTypeConstructorReference());
        }
    }

    private static Optional<JavaClassMacro> lookupClassMacro(Type type) {
        if (type instanceof ConstructedType constructedType) {
            type = constructedType.constructor().genericType();
        }
        return Optional.ofNullable(CLASS_MACROS.get(type));
    }
}
