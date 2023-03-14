package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.has;

public class UntypedCallNodeMatcher extends CastMatcher<Object, UntypedCallNode> {
    private final List<Matcher<? super UntypedCallNode>> matchers;

    public UntypedCallNodeMatcher(List<Matcher<? super UntypedCallNode>> matchers) {
        super(UntypedCallNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedCallNodeMatcher withReceiver(Matcher<? super UntypedExpressionNode> receiver) {
        return addMatcher(has("receiver", x -> x.receiver(), receiver));
    }

    public UntypedCallNodeMatcher withTypeLevelArgs(Matcher<Iterable<UntypedTypeLevelExpressionNode>> typeLevelArgs) {
        return addMatcher(has("typeLevelArgs", x -> x.typeLevelArgs(), typeLevelArgs));
    }

    public UntypedCallNodeMatcher withPositionalArgs(Matcher<Iterable<UntypedExpressionNode>> positionalArgs) {
        return addMatcher(has("positionalArgs", x -> x.positionalArgs(), positionalArgs));
    }

    public UntypedCallNodeMatcher withNamedArgs(Matcher<Iterable<UntypedNamedArgNode>> namedArgs) {
        return addMatcher(has("namedArgs", x -> x.namedArgs(), namedArgs));
    }

    private UntypedCallNodeMatcher addMatcher(Matcher<UntypedCallNode> matcher) {
        return new UntypedCallNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
