package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptExpressionNode;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.Optional;

public interface TypeScriptClassMacro {
    Type receiverType();
    TypeScriptExpressionNode compileConstructorCall(
        Optional<List<TypeScriptExpressionNode>> typeArgs,
        List<TypeScriptExpressionNode> positionalArgs
    );
    TypeScriptExpressionNode compileMethodCall(
        TypeScriptExpressionNode receiver,
        String methodName,
        List<TypeScriptExpressionNode> positionalArgs
    );
}
