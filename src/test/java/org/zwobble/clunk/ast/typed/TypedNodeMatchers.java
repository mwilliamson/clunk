package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;

import static org.zwobble.clunk.matchers.CastMatcher.cast;

public class TypedNodeMatchers {
    public static Matcher<TypedNamespaceStatementNode> isTypedRecordNode(Matcher<TypedRecordNode> matcher) {
        return cast(TypedRecordNode.class, matcher);
    }
}
