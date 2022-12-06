package org.zwobble.clunk.backends.python.codegenerator.macros;

import org.zwobble.clunk.backends.python.ast.PythonAttrAccessNode;
import org.zwobble.clunk.backends.python.ast.PythonCallNode;
import org.zwobble.clunk.backends.python.ast.PythonExpressionNode;
import org.zwobble.clunk.backends.python.ast.PythonListNode;
import org.zwobble.clunk.backends.python.codegenerator.PythonClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

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
        var listResult = PythonListMacro.INSTANCE.tryCompileMethodCall(receiver, methodName, positionalArgs);
        if (listResult.isPresent()) {
            return listResult.get();
        }

        switch (methodName) {
            case "add":
                return new PythonCallNode(
                    new PythonAttrAccessNode(receiver, "append"),
                    positionalArgs,
                    List.of()
                );
            default:
                throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }
}