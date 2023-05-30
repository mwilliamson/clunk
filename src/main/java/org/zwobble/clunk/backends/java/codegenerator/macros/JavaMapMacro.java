package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.backends.java.codegenerator.JavaClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public class JavaMapMacro implements JavaClassMacro  {
    public static final JavaMapMacro INSTANCE = new JavaMapMacro();

    private JavaMapMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.MAP_CONSTRUCTOR.genericType();
    }

    @Override
    public JavaTypeExpressionNode compileTypeConstructorReference() {
        return new JavaFullyQualifiedTypeReferenceNode("java.util", "Map");
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
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaExpressionNode compileMethodCall(
        JavaExpressionNode receiver,
        String methodName,
        List<JavaExpressionNode> positionalArgs
    ) {
        switch (methodName) {
            case "get" -> {
                return new JavaCallNode(
                    // TODO: generate this properly
                    new JavaReferenceNode("java.util.Optional.ofNullable"),
                    List.of(
                        new JavaCallNode(
                            new JavaMemberAccessNode(
                                receiver,
                                "get"
                            ),
                            positionalArgs
                        )
                    )
                );
            }
            default -> {
                throw new UnsupportedOperationException("unexpected method: " + methodName);
            }
        }
    }
}
