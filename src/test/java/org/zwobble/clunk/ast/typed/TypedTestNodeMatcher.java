package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedTestNodeMatcher extends CastMatcher<Object, TypedTestNode> {
    private final List<Matcher<? super TypedTestNode>> matchers;

    public TypedTestNodeMatcher(List<Matcher<? super TypedTestNode>> matchers) {
        super(TypedTestNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedTestNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public TypedTestNodeMatcher withBody(Matcher<Iterable<? extends TypedFunctionStatementNode>> body) {
        return addMatcher(has("body", body));
    }

    private TypedTestNodeMatcher addMatcher(Matcher<TypedTestNode> matcher) {
        return new TypedTestNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
