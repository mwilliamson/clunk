package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class UntypedCallNodeMatcher extends CastMatcher<Object, UntypedCallNode> {
    private final List<Matcher<? super UntypedCallNode>> matchers;

    public UntypedCallNodeMatcher(List<Matcher<? super UntypedCallNode>> matchers) {
        super(UntypedCallNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedCallNodeMatcher withReceiver(Matcher<? super UntypedExpressionNode> receiver) {
        return addMatcher(has("receiver", receiver));
    }

    public UntypedCallNodeMatcher withPositionalArgs(Matcher<? extends Iterable<? extends UntypedExpressionNode>> positionalArgs) {
        return addMatcher(has("positionalArgs", positionalArgs));
    }

    private UntypedCallNodeMatcher addMatcher(Matcher<UntypedCallNode> matcher) {
        return new UntypedCallNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
