package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class NodeMatchers {
    public static Matcher<UntypedNamespaceStatementNode> isRecordNode(Matcher<UntypedRecordNode> matcher) {
        return cast(UntypedRecordNode.class, matcher);
    }

    public static Matcher<UntypedStaticExpressionNode> isStaticReferenceNode(String value) {
        return cast(UntypedStaticExpressionNode.class, has("value", equalTo(value)));
    }
}
