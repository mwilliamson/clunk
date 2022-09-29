package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class UntypedTestSuiteNodeMatcher extends CastMatcher<Object, UntypedTestSuiteNode> {
    private final List<Matcher<? super UntypedTestSuiteNode>> matchers;

    public UntypedTestSuiteNodeMatcher(List<Matcher<? super UntypedTestSuiteNode>> matchers) {
        super(UntypedTestSuiteNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedTestSuiteNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public UntypedTestSuiteNodeMatcher withBody(Matcher<? extends Iterable<? extends UntypedNamespaceStatementNode>> body) {
        return addMatcher(has("body", body));
    }

    private UntypedTestSuiteNodeMatcher addMatcher(Matcher<UntypedTestSuiteNode> matcher) {
        return new UntypedTestSuiteNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
