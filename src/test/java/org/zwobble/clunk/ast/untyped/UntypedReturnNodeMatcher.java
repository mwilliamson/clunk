package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class UntypedReturnNodeMatcher extends CastMatcher<Object, UntypedReturnNode> {
    private final List<Matcher<? super UntypedReturnNode>> matchers;

    public UntypedReturnNodeMatcher(List<Matcher<? super UntypedReturnNode>> matchers) {
        super(UntypedReturnNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedReturnNodeMatcher withExpression(Matcher<UntypedExpressionNode> expression) {
        return addMatcher(has("expression", expression));
    }

    private UntypedReturnNodeMatcher addMatcher(Matcher<UntypedReturnNode> matcher) {
        return new UntypedReturnNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
