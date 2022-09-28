package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PythonMacros {
    private PythonMacros() {
    }

    private static final Map<Type, PythonClassMacro> CLASS_MACROS = Stream.of(
        new PythonClassMacro() {
            @Override
            public Type receiverType() {
                return Types.LIST_CONSTRUCTOR.genericType();
            }

            @Override
            public PythonExpressionNode compileConstructorCall(List<PythonExpressionNode> positionalArgs) {
                return new PythonListNode(List.of());
            }

            @Override
            public PythonExpressionNode compileMethodCall(PythonExpressionNode receiver, String methodName, List<PythonExpressionNode> positionalArgs) {
                switch (methodName) {
                    case "flatMap":
                        // TODO: Need to guarantee this doesn't collide -- we don't
                        // allow variables to start with an underscore, so this should
                        // be safe, but a little more rigour would probably be wise
                        // e.g. keeping track of variables in scope.
                        // Also, this probably doesn't work well with nested flatMaps.
                        var inputElementName = "_element";
                        var outputElementName = "_result";
                        var func = positionalArgs.get(0);

                        return new PythonListComprehensionNode(
                            new PythonReferenceNode(outputElementName),
                            List.of(
                                new PythonComprehensionForClauseNode(
                                    inputElementName,
                                    receiver
                                ),
                                new PythonComprehensionForClauseNode(
                                    outputElementName,
                                    new PythonCallNode(
                                        func,
                                        List.of(new PythonReferenceNode(inputElementName)),
                                        List.of()
                                    )
                                )
                            )
                        );
                    case "length":
                        return new PythonCallNode(
                            new PythonReferenceNode("len"),
                            List.of(receiver),
                            List.of()
                        );
                    default:
                        throw new UnsupportedOperationException("unexpected method: " + methodName);
                }
            }
        },
        new PythonClassMacro() {
            @Override
            public Type receiverType() {
                return Types.STRING_BUILDER;
            }

            @Override
            public PythonExpressionNode compileConstructorCall(List<PythonExpressionNode> positionalArgs) {
                return new PythonListNode(List.of());
            }

            @Override
            public PythonExpressionNode compileMethodCall(PythonExpressionNode receiver, String methodName, List<PythonExpressionNode> positionalArgs) {
                switch (methodName) {
                    case "append":
                        return new PythonCallNode(
                            new PythonAttrAccessNode(receiver, "append"),
                            positionalArgs,
                            List.of()
                        );
                    case "build":
                        return new PythonCallNode(
                            new PythonAttrAccessNode(
                                new PythonStringLiteralNode(""),
                                "join"
                            ),
                            List.of(receiver),
                            List.of()
                        );
                    default:
                        throw new UnsupportedOperationException("unexpected method: " + methodName);
                }
            }
        }
    ).collect(Collectors.toMap(x -> x.receiverType(), x -> x));

    public static Optional<PythonClassMacro> lookupClassMacro(Type type) {
        if (type instanceof ConstructedType constructedType) {
            type = constructedType.constructor().genericType();
        }

        return Optional.ofNullable(CLASS_MACROS.get(type));
    }

    private static final Map<NamespaceName, Map<String, PythonStaticFunctionMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            NamespaceName.fromParts("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileReceiver(PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "assert_that"));
                        return new PythonReferenceNode("assert_that");
                    }
                })
            )
        ),
        Map.entry(
            NamespaceName.fromParts("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileReceiver(PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "equal_to"));
                        return new PythonReferenceNode("equal_to");
                    }
                })
            )
        )
    );

    public static Optional<PythonStaticFunctionMacro> lookupStaticFunctionMacro(Type type) {
        if (type instanceof StaticFunctionType staticFunctionType) {
            var macro = STATIC_FUNCTION_MACROS.getOrDefault(staticFunctionType.namespaceName(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }
}
