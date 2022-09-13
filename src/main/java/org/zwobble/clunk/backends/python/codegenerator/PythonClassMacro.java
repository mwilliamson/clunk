package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.backends.python.ast.PythonExpressionNode;
import org.zwobble.clunk.types.Type;

import java.util.List;

public interface PythonClassMacro {
    Type receiverType();
    PythonExpressionNode compileConstructorCall(List<PythonExpressionNode> positionalArgs);
    PythonExpressionNode compileMethodCall(PythonExpressionNode receiver, String methodName, List<PythonExpressionNode> positionalArgs);
}
