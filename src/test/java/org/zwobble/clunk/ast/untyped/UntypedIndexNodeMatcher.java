package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.pcollections.PVector;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class UntypedIndexNodeMatcher extends CastMatcher<Object, UntypedIndexNode> {
    private final PVector<Matcher<? super UntypedIndexNode>> matchers;

    public UntypedIndexNodeMatcher(PVector<Matcher<? super UntypedIndexNode>> matchers) {
        super(UntypedIndexNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedIndexNodeMatcher withReceiver(Matcher<? super UntypedExpressionNode> receiver) {
        return addMatcher(has("receiver", receiver));
    }

    public UntypedIndexNodeMatcher withIndex(Matcher<? super UntypedExpressionNode> index) {
        return addMatcher(has("index", index));
    }

    private UntypedIndexNodeMatcher addMatcher(Matcher<UntypedIndexNode> matcher) {
        return new UntypedIndexNodeMatcher(matchers.plus(matcher));
    }
}
