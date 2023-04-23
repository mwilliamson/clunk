package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.backends.python.codegenerator.macros.PythonListMacro;
import org.zwobble.clunk.backends.python.codegenerator.macros.PythonMutableListMacro;
import org.zwobble.clunk.backends.python.codegenerator.macros.PythonStringBuilderMacro;
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
        PythonListMacro.INSTANCE,
        PythonMutableListMacro.INSTANCE,
        PythonStringBuilderMacro.INSTANCE
    ).collect(Collectors.toMap(x -> x.receiverType(), x -> x));

    public static Optional<PythonClassMacro> lookupClassMacro(Type type) {
        if (type instanceof ConstructedType constructedType) {
            type = constructedType.constructor().genericType();
        }

        return Optional.ofNullable(CLASS_MACROS.get(type));
    }

    private static final Map<NamespaceId, Map<String, PythonStaticFunctionMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            Types.BUILTIN_NAMESPACE_ID,
            Map.ofEntries(
                Map.entry("none", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileCall(PythonArgsNode args, PythonCodeGeneratorContext context) {
                        return new PythonNoneLiteralNode();
                    }
                })
            )
        ),
        Map.entry(
            NamespaceId.source("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileCall(PythonArgsNode args, PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "assert_that"));
                        return new PythonCallNode(new PythonReferenceNode("assert_that"), args);
                    }
                })
            )
        ),
        Map.entry(
            NamespaceId.source("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileCall(PythonArgsNode args, PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "equal_to"));
                        return new PythonCallNode(new PythonReferenceNode("equal_to"), args);
                    }
                }),
                Map.entry("hasMember", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileCall(PythonArgsNode args, PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "has_attrs"));
                        var memberName = (PythonStringLiteralNode) args.positional().get(0);
                        var memberMatcher = args.positional().get(1);
                        return new PythonCallNode(
                            new PythonReferenceNode("has_attrs"),
                            new PythonArgsNode(
                                List.of(),
                                List.of(new PythonKeywordArgumentNode(memberName.value(), memberMatcher))
                            )
                        );
                    }
                }),
                Map.entry("isNone", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileCall(PythonArgsNode args, PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "equal_to"));
                        return new PythonCallNode(
                            new PythonReferenceNode("equal_to"),
                            new PythonArgsNode(
                                List.of(new PythonNoneLiteralNode()),
                                List.of()
                            )
                        );
                    }
                }),
                Map.entry("isSome", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileCall(PythonArgsNode args, PythonCodeGeneratorContext context) {
                        return args.positional().get(0);
                    }
                })
            )
        )
    );

    public static Optional<PythonStaticFunctionMacro> lookupStaticFunctionMacro(Type type) {
        if (type instanceof StaticFunctionType staticFunctionType) {
            var macro = STATIC_FUNCTION_MACROS.getOrDefault(staticFunctionType.namespaceId(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }
}
