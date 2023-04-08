package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.JavaExpressionNode;

import java.util.List;

interface JavaStaticFunctionMacro {
    JavaExpressionNode compileCall(
        List<JavaExpressionNode> args,
        JavaCodeGeneratorContext context
    );
}
