package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptExpressionNode;
import org.zwobble.clunk.types.Type;

import java.util.List;

public interface TypeScriptClassMacro {
    Type receiverType();
    TypeScriptExpressionNode compileConstructorCall(List<TypeScriptExpressionNode> positionalArgs);
    TypeScriptExpressionNode compileMethodCall(
        TypeScriptExpressionNode receiver,
        String methodName,
        List<TypeScriptExpressionNode> positionalArgs
    );
}
