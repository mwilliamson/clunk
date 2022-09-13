package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.backends.python.ast.PythonExpressionNode;

interface PythonStaticFunctionMacro {
    PythonExpressionNode compileReceiver(PythonCodeGeneratorContext context);
}
