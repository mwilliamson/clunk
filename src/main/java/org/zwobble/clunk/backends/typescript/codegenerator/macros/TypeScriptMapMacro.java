package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptCallNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptExpressionNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptPropertyAccessNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptReferenceNode;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptClassMacro;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGeneratorContext;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public class TypeScriptMapMacro implements TypeScriptClassMacro {
    public final static TypeScriptMapMacro INSTANCE = new TypeScriptMapMacro();

    private TypeScriptMapMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.MAP_CONSTRUCTOR.genericType();
    }

    @Override
    public TypeScriptExpressionNode compileTypeReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeScriptExpressionNode compileTypeConstructorReference() {
        return new TypeScriptReferenceNode("Map");
    }

    @Override
    public TypeScriptExpressionNode compileConstructorCall(
        Optional<List<TypeScriptExpressionNode>> typeArgs,
        List<TypeScriptExpressionNode> positionalArgs
    ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeScriptExpressionNode compileMethodCall(
        TypeScriptExpressionNode receiver,
        String methodName,
        List<TypeScriptExpressionNode> positionalArgs,
        TypeScriptCodeGeneratorContext context
    ) {
        switch (methodName) {
            case "get" -> {
                return new TypeScriptCallNode(
                    new TypeScriptPropertyAccessNode(
                        receiver,
                        "get"
                    ),
                    positionalArgs
                );
            }
            default -> {
                throw new UnsupportedOperationException("unexpected method: " + methodName);
            }
        }
    }
}
