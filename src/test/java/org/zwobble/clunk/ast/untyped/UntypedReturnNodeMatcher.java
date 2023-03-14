package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.has;

public class UntypedReturnNodeMatcher extends CastMatcher<Object, UntypedReturnNode> {
    private final List<Matcher<? super UntypedReturnNode>> matchers;

    public UntypedReturnNodeMatcher(List<Matcher<? super UntypedReturnNode>> matchers) {
        super(UntypedReturnNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedReturnNodeMatcher withExpression(Matcher<UntypedExpressionNode> expression) {
        return addMatcher(has("expression", x -> x.expression(), expression));
    }

    private UntypedReturnNodeMatcher addMatcher(Matcher<UntypedReturnNode> matcher) {
        return new UntypedReturnNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
