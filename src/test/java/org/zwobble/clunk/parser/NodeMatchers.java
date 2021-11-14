package org.zwobble.clunk.parser;

import org.hamcrest.Matcher;
import org.zwobble.clunk.ast.NamespaceStatementNode;
import org.zwobble.clunk.ast.RecordNode;

import static org.zwobble.clunk.parser.CastMatcher.cast;

public class NodeMatchers {
    public static Matcher<NamespaceStatementNode> isRecordNode(Matcher<RecordNode> matcher) {
        return cast(RecordNode.class, matcher);
    }
}
