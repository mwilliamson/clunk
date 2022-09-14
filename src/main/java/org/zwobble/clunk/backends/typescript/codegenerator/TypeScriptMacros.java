package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.python.ast.PythonAttrAccessNode;
import org.zwobble.clunk.backends.python.ast.PythonCallNode;
import org.zwobble.clunk.backends.python.ast.PythonExpressionNode;
import org.zwobble.clunk.backends.python.ast.PythonStringLiteralNode;
import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeScriptMacros {
    private TypeScriptMacros() {
    }

    private static final Map<Type, TypeScriptClassMacro> CLASS_MACROS = Stream.of(
        new TypeScriptClassMacro() {
            @Override
            public Type receiverType() {
                return Types.STRING_BUILDER;
            }

            @Override
            public TypeScriptExpressionNode compileConstructorCall(List<TypeScriptExpressionNode> positionalArgs) {
                return new TypeScriptArrayNode(List.of());
            }

            @Override
            public TypeScriptExpressionNode compileMethodCall(
                TypeScriptExpressionNode receiver,
                String methodName,
                List<TypeScriptExpressionNode> positionalArgs
            ) {
                switch (methodName) {
                    case "append":
                        return new TypeScriptCallNode(
                            new TypeScriptPropertyAccessNode(receiver, "push"),
                            positionalArgs
                        );
                    case "build":
                        return new TypeScriptCallNode(
                            new TypeScriptPropertyAccessNode(
                                receiver,
                                "join"
                            ),
                            List.of(new TypeScriptStringLiteralNode(""))
                        );
                    default:
                        throw new UnsupportedOperationException("unexpected method: " + methodName);
                }
            }
        }
    ).collect(Collectors.toMap(x -> x.receiverType(), x -> x));

    public static Optional<TypeScriptClassMacro> lookupClassMacro(Type type) {
        return Optional.ofNullable(CLASS_MACROS.get(type));
    }

    private static final Map<NamespaceName, Map<String, TypeScriptStaticFunctionMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            NamespaceName.fromParts("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new TypeScriptStaticFunctionMacro() {
                    @Override
                    public TypeScriptExpressionNode compileReceiver(TypeScriptCodeGeneratorContext context) {
                        context.addImport("@mwilliamson/precisely", "assertThat");
                        return new TypeScriptReferenceNode("assertThat");
                    }
                })
            )
        ),
        Map.entry(
            NamespaceName.fromParts("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new TypeScriptStaticFunctionMacro() {
                    @Override
                    public TypeScriptExpressionNode compileReceiver(TypeScriptCodeGeneratorContext context) {
                        context.addImport("@mwilliamson/precisely", "equalTo");
                        return new TypeScriptReferenceNode("equalTo");
                    }
                })
            )
        )
    );

    public static Optional<TypeScriptStaticFunctionMacro> lookupStaticFunctionMacro(StaticFunctionType staticFunctionType) {
        var macro = STATIC_FUNCTION_MACROS.getOrDefault(staticFunctionType.namespaceName(), Map.of())
            .get(staticFunctionType.functionName());
        return Optional.ofNullable(macro);
    }
}
