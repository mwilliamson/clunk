package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public class Python {
    public static final PythonExpressionNode FALSE = new PythonBoolLiteralNode(false);
    public static final PythonExpressionNode TRUE = new PythonBoolLiteralNode(true);

    private Python() {

    }

    public static PythonAttrAccessNode attr(PythonExpressionNode receiver, String attrName) {
        return new PythonAttrAccessNode(receiver, attrName);
    }

    public static PythonCallNode call(PythonReferenceNode receiver, List<PythonKeywordArgumentNode> kwargs) {
        return new PythonCallNode(receiver, kwargs);
    }

    public static PythonKeywordArgumentNode kwarg(String name, PythonExpressionNode expression) {
        return new PythonKeywordArgumentNode(name, expression);
    }

    public static PythonReferenceNode reference(String name) {
        return new PythonReferenceNode(name);
    }

    public static PythonAssignmentNode variableType(String name, PythonExpressionNode type) {
        return new PythonAssignmentNode(name, type);
    }
}
