package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.JavaExpressionNode;

interface JavaStaticFunctionMacro {
    JavaExpressionNode compileReceiver(JavaCodeGeneratorContext context);
}
