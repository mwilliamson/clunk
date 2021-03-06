package org.zwobble.clunk.backends.python.ast;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class Python {
    public static final PythonExpressionNode FALSE = new PythonBoolLiteralNode(false);
    public static final PythonExpressionNode TRUE = new PythonBoolLiteralNode(true);

    private Python() {

    }

    public static PythonAddNode add(PythonExpressionNode left, PythonExpressionNode right) {
        return new PythonAddNode(left, right);
    }

    public static PythonAssertNode assert_(PythonExpressionNode expression) {
        return new PythonAssertNode(expression);
    }

    public static PythonAttrAccessNode attr(PythonExpressionNode receiver, String attrName) {
        return new PythonAttrAccessNode(receiver, attrName);
    }

    public static PythonCallNode call(
        PythonExpressionNode receiver,
        List<PythonExpressionNode> args,
        List<PythonKeywordArgumentNode> kwargs
    ) {
        return new PythonCallNode(receiver, args, kwargs);
    }

    public static PythonCallNode call(PythonExpressionNode receiver, List<PythonKeywordArgumentNode> kwargs) {
        return new PythonCallNode(receiver, List.of(), kwargs);
    }

    public static PythonConditionalBranchNode conditionalBranch(
        PythonExpressionNode condition,
        List<PythonStatementNode> body
    ) {
        return new PythonConditionalBranchNode(condition, body);
    }

    public static PythonExpressionStatementNode expressionStatement(PythonExpressionNode expression) {
        return new PythonExpressionStatementNode(expression);
    }

    public static PythonKeywordArgumentNode kwarg(String name, PythonExpressionNode expression) {
        return new PythonKeywordArgumentNode(name, expression);
    }

    public static PythonIfStatementNode ifStatement(
        List<PythonConditionalBranchNode> conditionalBranches,
        List<PythonStatementNode> elseBody
    ) {
        return new PythonIfStatementNode(conditionalBranches, elseBody);
    }

    public static PythonExpressionNode intLiteral(int value) {
        return new PythonIntLiteralNode(BigInteger.valueOf(value));
    }

    public static PythonStringLiteralNode string(String value) {
        return new PythonStringLiteralNode(value);
    }

    public static PythonReferenceNode reference(String name) {
        return new PythonReferenceNode(name);
    }

    public static PythonReturnNode returnStatement(PythonExpressionNode expression) {
        return new PythonReturnNode(expression);
    }

    public static PythonSingleLineCommentNode singleLineComment(String value) {
        return new PythonSingleLineCommentNode(value);
    }

    public static PythonExpressionNode subscription(
        PythonExpressionNode receiver,
        List<PythonExpressionNode> args
    ) {
        return new PythonSubscriptionNode(receiver, args);
    }

    public static PythonAssignmentNode variableType(String name, PythonExpressionNode type) {
        return new PythonAssignmentNode(name, Optional.of(type), Optional.empty());
    }
}
