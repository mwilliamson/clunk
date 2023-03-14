package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.has;

public class TypedReturnNodeMatcher extends CastMatcher<Object, TypedReturnNode> {
    private final List<Matcher<? super TypedReturnNode>> matchers;

    public TypedReturnNodeMatcher(List<Matcher<? super TypedReturnNode>> matchers) {
        super(TypedReturnNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedReturnNodeMatcher withExpression(Matcher<? super TypedExpressionNode> matcher) {
        return addMatcher(has("expression", x -> x.expression(), matcher));
    }

    private TypedReturnNodeMatcher addMatcher(Matcher<TypedReturnNode> matcher) {
        return new TypedReturnNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
