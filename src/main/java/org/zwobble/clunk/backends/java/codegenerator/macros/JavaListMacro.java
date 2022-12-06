package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.backends.java.codegenerator.JavaClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;

public class JavaListMacro implements JavaClassMacro  {
    public static final JavaListMacro INSTANCE = new JavaListMacro();

    private JavaListMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.LIST_CONSTRUCTOR.genericType();
    }

    @Override
    public JavaExpressionNode compileConstructorCall(List<JavaExpressionNode> positionalArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaExpressionNode compileMethodCall(JavaExpressionNode receiver, String methodName, List<JavaExpressionNode> positionalArgs) {
        switch (methodName) {
            case "flatMap":
                var func = positionalArgs.get(0);
                var stream = new JavaCallNode(
                    new JavaMemberAccessNode(receiver, "stream"),
                    List.of()
                );
                var map = new JavaCallNode(
                    new JavaMemberAccessNode(stream, "map"),
                    List.of(func)
                );
                var flatMap = new JavaCallNode(
                    new JavaMemberAccessNode(map, "flatMap"),
                    List.of(new JavaMethodReferenceStaticNode(
                        new JavaFullyQualifiedTypeReferenceNode("java.util", "List"),
                        "stream"
                    ))
                );
                return new JavaCallNode(
                    new JavaMemberAccessNode(flatMap, "toList"),
                    List.of()
                );
            case "get":
                return new JavaCallNode(
                    new JavaMemberAccessNode(receiver, "get"),
                    positionalArgs
                );
            case "length":
                return new JavaCallNode(
                    new JavaMemberAccessNode(receiver, "size"),
                    List.of()
                );
            default:
                throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }
}
