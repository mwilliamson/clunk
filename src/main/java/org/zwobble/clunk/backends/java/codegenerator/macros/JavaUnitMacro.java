package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.zwobble.clunk.backends.java.ast.JavaExpressionNode;
import org.zwobble.clunk.backends.java.ast.JavaTypeExpressionNode;
import org.zwobble.clunk.backends.java.ast.JavaTypeVariableReferenceNode;
import org.zwobble.clunk.backends.java.codegenerator.JavaClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public class JavaUnitMacro implements JavaClassMacro {
    public static final JavaUnitMacro INSTANCE = new JavaUnitMacro();

    private JavaUnitMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.UNIT;
    }

    @Override
    public JavaTypeExpressionNode compileTypeConstructorReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaTypeExpressionNode compileTypeReference() {
        return new JavaTypeVariableReferenceNode("void");
    }

    @Override
    public JavaExpressionNode compileConstructorCall(Optional<List<JavaTypeExpressionNode>> typeArgs, List<JavaExpressionNode> positionalArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaExpressionNode compileMethodCall(JavaExpressionNode receiver, String methodName, List<JavaExpressionNode> positionalArgs) {
        throw new UnsupportedOperationException("unexpected method: " + methodName);
    }
}
