package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedTestSuiteNodeMatcher extends CastMatcher<Object, TypedTestSuiteNode> {
    private final List<Matcher<? super TypedTestSuiteNode>> matchers;

    public TypedTestSuiteNodeMatcher(List<Matcher<? super TypedTestSuiteNode>> matchers) {
        super(TypedTestSuiteNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedTestSuiteNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public TypedTestSuiteNodeMatcher withBody(Matcher<Iterable<? extends TypedFunctionStatementNode>> body) {
        return addMatcher(has("body", body));
    }

    private TypedTestSuiteNodeMatcher addMatcher(Matcher<TypedTestSuiteNode> matcher) {
        return new TypedTestSuiteNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
