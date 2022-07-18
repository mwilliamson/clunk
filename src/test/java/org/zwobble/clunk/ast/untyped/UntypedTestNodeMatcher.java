package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class UntypedTestNodeMatcher extends CastMatcher<Object, UntypedTestNode> {
    private final List<Matcher<? super UntypedTestNode>> matchers;

    public UntypedTestNodeMatcher(List<Matcher<? super UntypedTestNode>> matchers) {
        super(UntypedTestNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedTestNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public UntypedTestNodeMatcher withBody(Matcher<? extends Iterable<? extends UntypedFunctionStatementNode>> body) {
        return addMatcher(has("body", body));
    }

    private UntypedTestNodeMatcher addMatcher(Matcher<UntypedTestNode> matcher) {
        return new UntypedTestNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
