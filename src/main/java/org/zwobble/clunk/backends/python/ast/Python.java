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

    public static PythonBoolAndNode boolAnd(PythonExpressionNode left, PythonExpressionNode right) {
        return new PythonBoolAndNode(left, right);
    }

    public static PythonBoolNotNode boolNot(PythonExpressionNode operand) {
        return new PythonBoolNotNode(operand);
    }

    public static PythonBoolOrNode boolOr(PythonExpressionNode left, PythonExpressionNode right) {
        return new PythonBoolOrNode(left, right);
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

    public static PythonComprehensionForClauseNode comprehensionForClause(
        String target,
        PythonExpressionNode iterable
    ) {
        return new PythonComprehensionForClauseNode(target, iterable);
    }

    public static PythonConditionalBranchNode conditionalBranch(
        PythonExpressionNode condition,
        List<PythonStatementNode> body
    ) {
        return new PythonConditionalBranchNode(condition, body);
    }

    public static PythonDictNode dict(List<PythonDictItemNode> items) {
        return new PythonDictNode(items);
    }

    public static PythonDictItemNode dictItem(PythonExpressionNode key, PythonExpressionNode value) {
        return new PythonDictItemNode(key, value);
    }

    public static PythonEqualsNode equals(PythonExpressionNode left, PythonExpressionNode right) {
        return new PythonEqualsNode(left, right);
    }

    public static PythonExpressionStatementNode expressionStatement(PythonExpressionNode expression) {
        return new PythonExpressionStatementNode(expression);
    }

    public static PythonForEachNode forEach(
        String targetName,
        PythonExpressionNode iterable,
        List<PythonStatementNode> body
    ) {
        return new PythonForEachNode(targetName, iterable, body);
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

    public static PythonListNode list(List<PythonExpressionNode> elements) {
        return new PythonListNode(elements);
    }

    public static PythonListComprehensionNode listComprehension(
        PythonExpressionNode element,
        List<PythonComprehensionForClauseNode> forClauses
    ) {
        return new PythonListComprehensionNode(element, forClauses);
    }

    public static PythonNotEqualNode notEqual(PythonExpressionNode left, PythonExpressionNode right) {
        return new PythonNotEqualNode(left, right);
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
