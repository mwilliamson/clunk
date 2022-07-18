package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedVarNodeMatcher extends CastMatcher<Object, TypedVarNode> {
    private final List<Matcher<? super TypedVarNode>> matchers;

    public TypedVarNodeMatcher(List<Matcher<? super TypedVarNode>> matchers) {
        super(TypedVarNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedVarNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public TypedVarNodeMatcher withExpression(Matcher<TypedExpressionNode> expression) {
        return addMatcher(has("expression", expression));
    }

    private TypedVarNodeMatcher addMatcher(Matcher<TypedVarNode> matcher) {
        return new TypedVarNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
