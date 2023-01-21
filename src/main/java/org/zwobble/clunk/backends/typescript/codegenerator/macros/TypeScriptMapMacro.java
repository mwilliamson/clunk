package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptClassMacro;
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
        List<TypeScriptExpressionNode> positionalArgs
    ) {
        throw new UnsupportedOperationException("unexpected method: " + methodName);
    }
}
