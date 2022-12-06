package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.backends.java.codegenerator.JavaClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;

public class JavaMutableListMacro implements JavaClassMacro {
    public static final JavaMutableListMacro INSTANCE = new JavaMutableListMacro();

    private JavaMutableListMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.MUTABLE_LIST_CONSTRUCTOR.genericType();
    }

    @Override
    public JavaExpressionNode compileConstructorCall(List<JavaExpressionNode> positionalArgs) {
        throw new UnsupportedOperationException();
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
