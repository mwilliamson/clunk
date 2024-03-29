package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.JavaExpressionNode;
import org.zwobble.clunk.backends.java.ast.JavaTypeExpressionNode;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.Optional;

public interface JavaClassMacro {
    Type receiverType();
    // TODO: split interface depending on whether this is a generic type?
    JavaTypeExpressionNode compileTypeConstructorReference();
    JavaTypeExpressionNode compileTypeReference();
    JavaExpressionNode compileConstructorCall(
        Optional<List<JavaTypeExpressionNode>> typeArgs,
        List<JavaExpressionNode> positionalArgs
    );
    JavaExpressionNode compileMethodCall(
        JavaExpressionNode receiver,
        String methodName,
        List<JavaExpressionNode> positionalArgs
    );
}
