package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class UntypedTestNodeMatcher extends CastMatcher<Object, UntypedTestNode> {
    private final List<Matcher<? super UntypedTestNode>> matchers;

    public UntypedTestNodeMatcher(List<Matcher<? super UntypedTestNode>> matchers) {
        super(UntypedTestNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedTestNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public UntypedTestNodeMatcher withBody(Matcher<Iterable<UntypedFunctionStatementNode>> body) {
        return addMatcher(has("body", x -> x.body(), body));
    }

    private UntypedTestNodeMatcher addMatcher(Matcher<UntypedTestNode> matcher) {
        return new UntypedTestNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
