package org.zwobble.clunk.ast.untyped;

import org.pcollections.PVector;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.precisely.Matcher;

import static org.zwobble.precisely.Matchers.has;

public class UntypedIndexNodeMatcher extends CastMatcher<Object, UntypedIndexNode> {
    private final PVector<Matcher<? super UntypedIndexNode>> matchers;

    public UntypedIndexNodeMatcher(PVector<Matcher<? super UntypedIndexNode>> matchers) {
        super(UntypedIndexNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedIndexNodeMatcher withReceiver(Matcher<? super UntypedExpressionNode> receiver) {
        return addMatcher(has("receiver", x -> x.receiver(), receiver));
    }

    public UntypedIndexNodeMatcher withIndex(Matcher<? super UntypedExpressionNode> index) {
        return addMatcher(has("index", x -> x.index(), index));
    }

    private UntypedIndexNodeMatcher addMatcher(Matcher<UntypedIndexNode> matcher) {
        return new UntypedIndexNodeMatcher(matchers.plus(matcher));
    }
}
