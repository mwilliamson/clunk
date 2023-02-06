package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.backends.python.ast.PythonArgsNode;
import org.zwobble.clunk.backends.python.ast.PythonExpressionNode;
import org.zwobble.clunk.types.Type;

public interface PythonClassMacro {
    Type receiverType();
    PythonExpressionNode compileConstructorCall(PythonArgsNode args);
    PythonExpressionNode compileMethodCall(PythonExpressionNode receiver, String methodName, PythonArgsNode args);
}
