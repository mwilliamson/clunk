package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class UntypedNodeMatchers {
    public static Matcher<UntypedNamespaceStatementNode> isUntypedFunctionNode(Matcher<UntypedFunctionNode>... matchers) {
        return cast(UntypedFunctionNode.class, matchers);
    }

    public static Matcher<UntypedFunctionNode> untypedFunctionNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<UntypedFunctionNode> untypedFunctionNodeHasReturnType(
        Matcher<UntypedStaticExpressionNode> matcher
    ) {
        return has("returnType", matcher);
    }

    public static Matcher<UntypedNamespaceStatementNode> isUntypedRecordNode(Matcher<UntypedRecordNode> matcher) {
        return cast(UntypedRecordNode.class, matcher);
    }

    public static Matcher<UntypedRecordNode> untypedRecordNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<UntypedStaticExpressionNode> isUntypedStaticReferenceNode(String value) {
        return cast(UntypedStaticExpressionNode.class, has("value", equalTo(value)));
    }

    public static Matcher<UntypedExpressionNode> isUntypedStringLiteralNode(Matcher<UntypedStringLiteralNode> matcher) {
        return cast(UntypedStringLiteralNode.class, matcher);
    }
}
