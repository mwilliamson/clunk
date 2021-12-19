package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class UntypedNodeMatchers {
    public static Matcher<UntypedExpressionNode> isUntypedBoolLiteralNode(boolean value) {
        return cast(UntypedBoolLiteralNode.class, has("value", equalTo(value)));
    }

    @SafeVarargs
    public static Matcher<UntypedNamespaceStatementNode> isUntypedFunctionNode(Matcher<UntypedFunctionNode>... matchers) {
        return cast(UntypedFunctionNode.class, matchers);
    }

    public static Matcher<UntypedFunctionNode> untypedFunctionNodeHasParams(Matcher<? extends Iterable<? extends UntypedParamNode>> matcher) {
        return has("params", matcher);
    }

    public static Matcher<UntypedFunctionNode> untypedFunctionNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<UntypedFunctionNode> untypedFunctionNodeHasReturnType(
        Matcher<UntypedStaticExpressionNode> matcher
    ) {
        return has("returnType", matcher);
    }

    public static Matcher<UntypedNamespaceNode> untypedNamespaceNodeHasStatements(
        Matcher<Iterable<? extends UntypedNamespaceStatementNode>> matcher
    ) {
        return has("statements", matcher);
    }

    @SafeVarargs
    public static Matcher<UntypedParamNode> isUntypedParamNode(Matcher<UntypedParamNode>... matchers) {
        return allOf(matchers);
    }

    public static Matcher<UntypedParamNode> untypedParamNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<UntypedParamNode> untypedParamNodeHasType(Matcher<UntypedStaticExpressionNode> type) {
        return has("type", type);
    }

    public static Matcher<UntypedNamespaceStatementNode> isUntypedRecordNode(Matcher<UntypedRecordNode> matcher) {
        return cast(UntypedRecordNode.class, matcher);
    }

    public static Matcher<UntypedRecordNode> untypedRecordNodeHasFields(
        Matcher<Iterable<? extends UntypedRecordFieldNode>> matcher
    ) {
        return has("fields", matcher);
    }

    public static Matcher<UntypedRecordNode> untypedRecordNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<UntypedRecordFieldNode> untypedRecordFieldNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<UntypedRecordFieldNode> untypedRecordFieldNodeHasType(
        Matcher<UntypedStaticExpressionNode> matcher
    ) {
        return has("type", matcher);
    }

    public static Matcher<UntypedFunctionStatementNode> isUntypedReturnNode(Matcher<UntypedReturnNode> matcher) {
        return cast(UntypedReturnNode.class, matcher);
    }

    public static Matcher<UntypedReturnNode> untypedReturnNodeHasExpression(Matcher<UntypedExpressionNode> expression) {
        return has("expression", expression);
    }

    public static Matcher<UntypedStaticExpressionNode> isUntypedStaticReferenceNode(String value) {
        return cast(UntypedStaticExpressionNode.class, has("value", equalTo(value)));
    }

    public static Matcher<UntypedExpressionNode> isUntypedStringLiteralNode(Matcher<UntypedStringLiteralNode> matcher) {
        return cast(UntypedStringLiteralNode.class, matcher);
    }
}
