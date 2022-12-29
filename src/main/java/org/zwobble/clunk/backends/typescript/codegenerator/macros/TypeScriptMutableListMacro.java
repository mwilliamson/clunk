package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public class TypeScriptMutableListMacro implements TypeScriptClassMacro {
    public final static TypeScriptMutableListMacro INSTANCE = new TypeScriptMutableListMacro();

    private TypeScriptMutableListMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.MUTABLE_LIST_CONSTRUCTOR.genericType();
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
        return new TypeScriptCallNewNode(
            TypeScript.reference("Array"),
            typeArgs.orElseThrow(),
            List.of()
        );
    }

    @Override
    public TypeScriptExpressionNode compileMethodCall(
        TypeScriptExpressionNode receiver,
        String methodName,
        List<TypeScriptExpressionNode> positionalArgs
    ) {

        var listResult = TypeScriptListMacro.INSTANCE.tryCompileMethodCall(receiver, methodName, positionalArgs);
        if (listResult.isPresent()) {
            return listResult.get();
        }

        switch (methodName) {
            case "add":
                return new TypeScriptCallNode(
                    new TypeScriptPropertyAccessNode(receiver, "push"),
                    positionalArgs
                );
            default:
                throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }
}
