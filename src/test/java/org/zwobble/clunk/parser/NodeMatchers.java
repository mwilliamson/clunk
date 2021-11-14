package org.zwobble.clunk.parser;

import org.hamcrest.Matcher;
import org.zwobble.clunk.ast.NamespaceStatementNode;
import org.zwobble.clunk.ast.RecordNode;
import org.zwobble.clunk.ast.StaticExpressionNode;

import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.parser.CastMatcher.cast;
import static org.zwobble.clunk.parser.HasRecordComponentWithValue.hasRecordComponent;

public class NodeMatchers {
    public static Matcher<NamespaceStatementNode> isRecordNode(Matcher<RecordNode> matcher) {
        return cast(RecordNode.class, matcher);
    }

    public static Matcher<StaticExpressionNode> isStaticReferenceNode(String value) {
        return cast(StaticExpressionNode.class, hasRecordComponent("value", equalTo(value)));
    }
}
