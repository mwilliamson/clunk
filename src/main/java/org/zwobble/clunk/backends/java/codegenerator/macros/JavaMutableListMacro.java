package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.backends.java.codegenerator.JavaClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public class JavaMutableListMacro implements JavaClassMacro {
    public static final JavaMutableListMacro INSTANCE = new JavaMutableListMacro();

    private JavaMutableListMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.MUTABLE_LIST_CONSTRUCTOR.genericType();
    }

    @Override
    public JavaTypeExpressionNode compileTypeConstructorReference() {
        return new JavaFullyQualifiedTypeReferenceNode("java.util", "List");
    }

    @Override
    public JavaTypeExpressionNode compileTypeReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaExpressionNode compileConstructorCall(
        Optional<List<JavaTypeExpressionNode>> typeArgs,
        List<JavaExpressionNode> positionalArgs
    ) {
        return new JavaCallNewNode(
            // TODO: proper fully qualified reference
            new JavaReferenceNode("java.util.ArrayList"),
            typeArgs,
            List.of(),
            Optional.empty()
        );
    }

    @Override
    public JavaExpressionNode compileMethodCall(
        JavaExpressionNode receiver,
        String methodName,
        List<JavaExpressionNode> positionalArgs
    ) {
        var result = JavaListMacro.INSTANCE.tryCompileMethodCall(receiver, methodName, positionalArgs);
        if (result.isPresent()) {
            return result.get();
        }

        switch (methodName) {
            case "add":
                return new JavaCallNode(
                    new JavaMemberAccessNode(
                        receiver,
                        "add"
                    ),
                    positionalArgs
                );
            default:
                throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }
}
