package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.backends.python.ast.PythonArgsNode;
import org.zwobble.clunk.backends.python.ast.PythonExpressionNode;

interface PythonStaticFunctionMacro {
    PythonExpressionNode compileCall(
        PythonArgsNode args,
        PythonCodeGeneratorContext context
    );
}
