package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptExpressionNode;

public interface TypeScriptStaticFunctionMacro {
    TypeScriptExpressionNode compileReceiver(TypeScriptCodeGeneratorContext context);
}
