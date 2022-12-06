package org.zwobble.clunk.backends.python.codegenerator.macros;

import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.backends.python.codegenerator.PythonClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;

public class PythonListMacro implements PythonClassMacro {
    public static final PythonListMacro INSTANCE = new PythonListMacro();

    private PythonListMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.LIST_CONSTRUCTOR.genericType();
    }

    @Override
    public PythonExpressionNode compileConstructorCall(List<PythonExpressionNode> positionalArgs) {
        return new PythonListNode(List.of());
    }

    @Override
    public PythonExpressionNode compileMethodCall(PythonExpressionNode receiver, String methodName, List<PythonExpressionNode> positionalArgs) {
        switch (methodName) {
            case "flatMap":
                // TODO: Need to guarantee this doesn't collide -- we don't
                // allow variables to start with an underscore, so this should
                // be safe, but a little more rigour would probably be wise
                // e.g. keeping track of variables in scope.
                // Also, this probably doesn't work well with nested flatMaps.
                // TODO: avoid evaluating func multiple times
                var inputElementName = "_element";
                var outputElementName = "_result";
                var func = positionalArgs.get(0);

                return new PythonListComprehensionNode(
                    new PythonReferenceNode(outputElementName),
                    List.of(
                        new PythonComprehensionForClauseNode(
                            inputElementName,
                            receiver
                        ),
                        new PythonComprehensionForClauseNode(
                            outputElementName,
                            new PythonCallNode(
                                func,
                                List.of(new PythonReferenceNode(inputElementName)),
                                List.of()
                            )
                        )
                    )
                );
            case "get":
                return new PythonSubscriptionNode(receiver, positionalArgs);
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
