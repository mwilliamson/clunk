package org.zwobble.clunk.backends.python.codegenerator.macros;

import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.backends.python.codegenerator.PythonClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;

public class PythonStringBuilderMacro implements PythonClassMacro {
    public static PythonStringBuilderMacro INSTANCE = new PythonStringBuilderMacro();

    private PythonStringBuilderMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.STRING_BUILDER;
    }

    @Override
    public PythonExpressionNode compileConstructorCall(List<PythonExpressionNode> positionalArgs) {
        return new PythonListNode(List.of());
    }

    @Override
    public PythonExpressionNode compileMethodCall(PythonExpressionNode receiver, String methodName, List<PythonExpressionNode> positionalArgs) {
        switch (methodName) {
            case "append":
                return new PythonCallNode(
                    new PythonAttrAccessNode(receiver, "append"),
                    positionalArgs,
                    List.of()
                );
            case "build":
                return new PythonCallNode(
                    new PythonAttrAccessNode(
                        new PythonStringLiteralNode(""),
                        "join"
                    ),
                    List.of(receiver),
                    List.of()
                );
            default:
                throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }
}
