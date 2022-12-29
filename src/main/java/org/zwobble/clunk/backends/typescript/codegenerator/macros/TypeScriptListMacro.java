package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public class TypeScriptListMacro implements TypeScriptClassMacro {
    public final static TypeScriptListMacro INSTANCE = new TypeScriptListMacro();

    private TypeScriptListMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.LIST_CONSTRUCTOR.genericType();
    }

    @Override
    public TypeScriptExpressionNode compileTypeReference() {
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
        var result = tryCompileMethodCall(receiver, methodName, positionalArgs);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }

    public Optional<TypeScriptExpressionNode> tryCompileMethodCall(
        TypeScriptExpressionNode receiver,
        String methodName,
        List<TypeScriptExpressionNode> positionalArgs
    ) {
        switch (methodName) {
            case "flatMap" -> {
                var result = new TypeScriptCallNode(
                    new TypeScriptPropertyAccessNode(receiver, "flatMap"),
                    positionalArgs
                );
                return Optional.of(result);
            }
            case "get" -> {
                var result = new TypeScriptIndexNode(receiver, positionalArgs.get(0));
                return Optional.of(result);
            }
            case "last" -> {
                var length = new TypeScriptPropertyAccessNode(
                    receiver,
                    "length"
                );
                var index = new TypeScriptSubtractNode(length, new TypeScriptNumberLiteralNode(1));
                var result = new TypeScriptIndexNode(receiver, index);
                return Optional.of(result);
            }
            case "length" -> {
                var result = new TypeScriptPropertyAccessNode(
                    receiver,
                    "length"
                );
                return Optional.of(result);
            }
            default -> {
                return Optional.empty();
            }
        }
    }
}
