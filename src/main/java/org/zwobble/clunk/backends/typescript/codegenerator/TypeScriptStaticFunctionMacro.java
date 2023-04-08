package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptExpressionNode;

import java.util.List;

public interface TypeScriptStaticFunctionMacro {
    TypeScriptExpressionNode compileCall(
        List<TypeScriptExpressionNode> args,
        TypeScriptCodeGeneratorContext context
    );
}
