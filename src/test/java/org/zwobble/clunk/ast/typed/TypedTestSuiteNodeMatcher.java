package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedTestSuiteNodeMatcher extends CastMatcher<Object, TypedTestSuiteNode> {
    private final List<Matcher<? super TypedTestSuiteNode>> matchers;

    public TypedTestSuiteNodeMatcher(List<Matcher<? super TypedTestSuiteNode>> matchers) {
        super(TypedTestSuiteNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedTestSuiteNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public TypedTestSuiteNodeMatcher withBody(Matcher<Iterable<TypedNamespaceStatementNode>> body) {
        return addMatcher(has("body", x -> x.body(), body));
    }

    private TypedTestSuiteNodeMatcher addMatcher(Matcher<TypedTestSuiteNode> matcher) {
        return new TypedTestSuiteNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
