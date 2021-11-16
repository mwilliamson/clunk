package org.zwobble.clunk.ast;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class NodeMatchers {
    public static Matcher<NamespaceStatementNode> isRecordNode(Matcher<RecordNode> matcher) {
        return cast(RecordNode.class, matcher);
    }

    public static Matcher<StaticExpressionNode> isStaticReferenceNode(String value) {
        return cast(StaticExpressionNode.class, has("value", equalTo(value)));
    }
}
