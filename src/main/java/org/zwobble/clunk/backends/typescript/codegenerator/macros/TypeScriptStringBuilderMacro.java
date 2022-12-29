package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public class TypeScriptStringBuilderMacro implements TypeScriptClassMacro {
    public static final TypeScriptStringBuilderMacro INSTANCE = new TypeScriptStringBuilderMacro();

    private TypeScriptStringBuilderMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.STRING_BUILDER;
    }

    @Override
    public TypeScriptExpressionNode compileTypeReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeScriptExpressionNode compileTypeConstructorReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeScriptExpressionNode compileConstructorCall(
        Optional<List<TypeScriptExpressionNode>> typeArgs,
        List<TypeScriptExpressionNode> positionalArgs
    ) {
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
