package org.zwobble.clunk.backends.python.codegenerator.macros;

import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.backends.python.codegenerator.PythonClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.math.BigInteger;
import java.util.List;

public class PythonMutableListMacro implements PythonClassMacro {
    public static final PythonMutableListMacro INSTANCE = new PythonMutableListMacro();

    private PythonMutableListMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.MUTABLE_LIST_CONSTRUCTOR.genericType();
    }

    @Override
    public PythonExpressionNode compileConstructorCall(List<PythonExpressionNode> positionalArgs) {
        return new PythonListNode(List.of());
    }

    @Override
    public PythonExpressionNode compileMethodCall(PythonExpressionNode receiver, String methodName, List<PythonExpressionNode> positionalArgs) {
        // TODO: remove duplication with List
        switch (methodName) {
            case "add":
                return new PythonCallNode(
                    new PythonAttrAccessNode(receiver, "append"),
                    positionalArgs,
                    List.of()
                );
            case "last":
                return new PythonSubscriptionNode(
                    receiver,
                    List.of(new PythonIntLiteralNode(BigInteger.valueOf(-1)))
                );
            case "length":
                return new PythonCallNode(
                    new PythonReferenceNode("len"),
                    List.of(receiver),
                    List.of()
                );
            default:
                throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }
}
