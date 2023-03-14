package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedTestNodeMatcher extends CastMatcher<Object, TypedTestNode> {
    private final List<Matcher<? super TypedTestNode>> matchers;

    public TypedTestNodeMatcher(List<Matcher<? super TypedTestNode>> matchers) {
        super(TypedTestNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedTestNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public TypedTestNodeMatcher withBody(Matcher<Iterable<TypedFunctionStatementNode>> body) {
        return addMatcher(has("body", x -> x.body(), body));
    }

    private TypedTestNodeMatcher addMatcher(Matcher<TypedTestNode> matcher) {
        return new TypedTestNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
