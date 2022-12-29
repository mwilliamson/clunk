package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;

public class TypeScriptListMacro implements TypeScriptClassMacro {
    public final static TypeScriptListMacro INSTANCE = new TypeScriptListMacro();

    private TypeScriptListMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.LIST_CONSTRUCTOR.genericType();
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
            case "flatMap":
                return new TypeScriptCallNode(
                    new TypeScriptPropertyAccessNode(receiver, "flatMap"),
                    positionalArgs
                );
            case "get":
                return new TypeScriptIndexNode(receiver, positionalArgs.get(0));
            case "last":
                var length = new TypeScriptPropertyAccessNode(
                    receiver,
                    "length"
                );
                var index = new TypeScriptSubtractNode(length, new TypeScriptNumberLiteralNode(1));
                return new TypeScriptIndexNode(receiver, index);
            case "length":
                return new TypeScriptPropertyAccessNode(
                    receiver,
                    "length"
                );
            default:
                throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }
}
