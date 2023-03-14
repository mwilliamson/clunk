package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedVarNodeMatcher extends CastMatcher<Object, TypedVarNode> {
    private final List<Matcher<? super TypedVarNode>> matchers;

    public TypedVarNodeMatcher(List<Matcher<? super TypedVarNode>> matchers) {
        super(TypedVarNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedVarNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public TypedVarNodeMatcher withExpression(Matcher<TypedExpressionNode> expression) {
        return addMatcher(has("expression", x -> x.expression(), expression));
    }

    private TypedVarNodeMatcher addMatcher(Matcher<TypedVarNode> matcher) {
        return new TypedVarNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
