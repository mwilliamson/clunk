package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class UntypedNodeMatchers {
    public static Matcher<UntypedExpressionNode> isUntypedBoolLiteralNode(boolean value) {
        return cast(UntypedBoolLiteralNode.class, has("value", equalTo(value)));
    }

    public static UntypedFunctionNodeMatcher isUntypedFunctionNode() {
        return new UntypedFunctionNodeMatcher(List.of());
    }

    public static UntypedNamespaceNodeMatcher isUntypedNamespaceNode() {
        return new UntypedNamespaceNodeMatcher(List.of());
    }

    public static UntypedParamNodeMatcher isUntypedParamNode() {
        return new UntypedParamNodeMatcher(List.of());
    }

    public static UntypedRecordNodeMatcher isUntypedRecordNode() {
        return new UntypedRecordNodeMatcher(List.of());
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
