package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.pcollections.PVector;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedIndexNodeMatcher extends CastMatcher<Object, TypedIndexNode> {
    private final PVector<Matcher<? super TypedIndexNode>> matchers;

    public TypedIndexNodeMatcher(PVector<Matcher<? super TypedIndexNode>> matchers) {
        super(TypedIndexNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedIndexNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", receiver));
    }

    public TypedIndexNodeMatcher withIndex(Matcher<? super TypedExpressionNode> index) {
        return addMatcher(has("index", index));
    }

    public TypedIndexNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedIndexNodeMatcher addMatcher(Matcher<TypedIndexNode> matcher) {
        return new TypedIndexNodeMatcher(matchers.plus(matcher));
    }
}
