package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypedReturnNodeMatcher extends CastMatcher<Object, TypedReturnNode> {
    private final List<Matcher<? super TypedReturnNode>> matchers;

    public TypedReturnNodeMatcher(List<Matcher<? super TypedReturnNode>> matchers) {
        super(TypedReturnNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedReturnNodeMatcher withExpression(Matcher<TypedExpressionNode> matcher) {
        return addMatcher(has("expression", matcher));
    }

    private TypedReturnNodeMatcher addMatcher(Matcher<TypedReturnNode> matcher) {
        return new TypedReturnNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
