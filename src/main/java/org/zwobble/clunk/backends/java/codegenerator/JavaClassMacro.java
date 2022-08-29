package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.JavaExpressionNode;
import org.zwobble.clunk.types.Type;

import java.util.List;

public interface JavaClassMacro {
    Type receiverType();
    JavaExpressionNode compileConstructorCall(List<JavaExpressionNode> positionalArgs);
    JavaExpressionNode compileMethodCall(JavaExpressionNode receiver, String methodName, List<JavaExpressionNode> positionalArgs);
}
