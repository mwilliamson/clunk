package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.backends.java.codegenerator.JavaClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public class JavaStringBuilderMacro implements JavaClassMacro {
    public static final JavaStringBuilderMacro INSTANCE = new JavaStringBuilderMacro();

    private JavaStringBuilderMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.STRING_BUILDER;
    }

    @Override
    public JavaTypeExpressionNode compileTypeConstructorReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaTypeExpressionNode compileTypeReference() {
        return new JavaFullyQualifiedTypeReferenceNode("java.lang", "StringBuilder");
    }

    @Override
    public JavaExpressionNode compileConstructorCall(
        Optional<List<JavaTypeExpressionNode>> typeArgs,
        List<JavaExpressionNode> positionalArgs
    ) {
        return new JavaCallNewNode(
            new JavaReferenceNode("StringBuilder"),
            Optional.empty(),
            positionalArgs,
            Optional.empty()
        );
    }

    @Override
    public JavaExpressionNode compileMethodCall(JavaExpressionNode receiver, String methodName, List<JavaExpressionNode> positionalArgs) {
        switch (methodName) {
            case "append":
                return new JavaCallNode(
                    new JavaMemberAccessNode(receiver, "append"),
                    positionalArgs
                );
            case "build":
                return new JavaCallNode(
                    new JavaMemberAccessNode(receiver, "toString"),
                    positionalArgs
                );
            default:
                throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }
}
