package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaMacros {
    private static final Map<NamespaceName, Map<String, JavaStaticFunctionMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            NamespaceName.fromParts("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new JavaStaticFunctionMacro() {
                    @Override
                    public JavaExpressionNode compileReceiver(JavaCodeGeneratorContext context) {
                        context.addImportStatic("org.hamcrest.MatcherAssert", "assertThat");
                        return new JavaReferenceNode("assertThat");
                    }
                })
            )
        ),
        Map.entry(
            NamespaceName.fromParts("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new JavaStaticFunctionMacro() {
                    @Override
                    public JavaExpressionNode compileReceiver(JavaCodeGeneratorContext context) {
                        context.addImportStatic("org.hamcrest.Matchers", "equalTo");
                        return new JavaReferenceNode("equalTo");
                    }
                })
            )
        )
    );

    private static final Map<Type, JavaClassMacro> CLASS_MACROS = Stream.of(
        new JavaClassMacro() {
            @Override
            public Type receiverType() {
                return Types.LIST_CONSTRUCTOR.genericType();
            }

            @Override
            public JavaExpressionNode compileConstructorCall(List<JavaExpressionNode> positionalArgs) {
                throw new UnsupportedOperationException();
            }

            @Override
            public JavaExpressionNode compileMethodCall(JavaExpressionNode receiver, String methodName, List<JavaExpressionNode> positionalArgs) {
                switch (methodName) {
                    case "flatMap":
                        var func = positionalArgs.get(0);
                        var stream = new JavaCallNode(
                            new JavaMemberAccessNode(receiver, "stream"),
                            List.of()
                        );
                        var map = new JavaCallNode(
                            new JavaMemberAccessNode(stream, "map"),
                            List.of(func)
                        );
                        var flatMap = new JavaCallNode(
                            new JavaMemberAccessNode(map, "flatMap"),
                            List.of(new JavaMethodReferenceStaticNode(
                                new JavaFullyQualifiedTypeReferenceNode("java.util", "List"),
                                "stream"
                            ))
                        );
                        return new JavaCallNode(
                            new JavaMemberAccessNode(flatMap, "toList"),
                            List.of()
                        );
                    default:
                        throw new UnsupportedOperationException("unexpected method: " + methodName);
                }
            }
        },
        new JavaClassMacro() {
            @Override
            public Type receiverType() {
                return Types.STRING_BUILDER;
            }

            @Override
            public JavaExpressionNode compileConstructorCall(List<JavaExpressionNode> positionalArgs) {
                return new JavaCallNewNode(
                    new JavaReferenceNode("StringBuilder"),
                    Optional.empty(),
                    positionalArgs,
                    Optional.empty()
                );
            }

            @Override
            public JavaExpressionNode compileMethodCall(JavaExpressionNode receiver, String methodName, List<JavaExpressionNode> positionalArgs) {
                switch (methodName) {
                    case "append":
                        return new JavaCallNode(
                            new JavaMemberAccessNode(receiver, "append"),
                            positionalArgs
                        );
                    case "build":
                        return new JavaCallNode(
                            new JavaMemberAccessNode(receiver, "toString"),
                            positionalArgs
                        );
                    default:
                        throw new UnsupportedOperationException("unexpected method: " + methodName);
                }
            }
        }
    ).collect(Collectors.toMap(x -> x.receiverType(), x -> x));

    public static Optional<JavaStaticFunctionMacro> lookupStaticFunctionMacro(StaticFunctionType receiverType) {
        var macro = STATIC_FUNCTION_MACROS.getOrDefault(receiverType.namespaceName(), Map.of())
            .get(receiverType.functionName());
        return Optional.ofNullable(macro);
    }

    public static Optional<JavaClassMacro> lookupClassMacro(Type type) {
        if (type instanceof ConstructedType constructedType) {
            type = constructedType.constructor().genericType();
        }
        return Optional.ofNullable(CLASS_MACROS.get(type));
    }
}
