package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class UntypedTestSuiteNodeMatcher extends CastMatcher<Object, UntypedTestSuiteNode> {
    private final List<Matcher<? super UntypedTestSuiteNode>> matchers;

    public UntypedTestSuiteNodeMatcher(List<Matcher<? super UntypedTestSuiteNode>> matchers) {
        super(UntypedTestSuiteNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedTestSuiteNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public UntypedTestSuiteNodeMatcher withBody(Matcher<Iterable<UntypedNamespaceStatementNode>> body) {
        return addMatcher(has("body", x -> x.body(), body));
    }

    private UntypedTestSuiteNodeMatcher addMatcher(Matcher<UntypedTestSuiteNode> matcher) {
        return new UntypedTestSuiteNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
