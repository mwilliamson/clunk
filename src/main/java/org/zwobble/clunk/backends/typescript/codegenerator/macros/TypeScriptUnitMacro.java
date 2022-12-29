package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptExpressionNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptReferenceNode;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public class TypeScriptUnitMacro implements TypeScriptClassMacro {
    public static final TypeScriptUnitMacro INSTANCE = new TypeScriptUnitMacro();

    private TypeScriptUnitMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.UNIT;
    }

    @Override
    public TypeScriptExpressionNode compileTypeReference() {
        return new TypeScriptReferenceNode("void");
    }

    @Override
    public TypeScriptExpressionNode compileTypeConstructorReference() {
        throw new UnsupportedOperationException();
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
