package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.pcollections.PVector;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedMemberAccessNodeMatcher extends CastMatcher<Object, TypedMemberAccessNode> {
    private final PVector<Matcher<? super TypedMemberAccessNode>> matchers;

    public TypedMemberAccessNodeMatcher(PVector<Matcher<? super TypedMemberAccessNode>> matchers) {
        super(TypedMemberAccessNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedMemberAccessNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", receiver));
    }

    public TypedMemberAccessNodeMatcher withMemberName(Matcher<? super String> memberName) {
        return addMatcher(has("memberName", memberName));
    }

    public TypedMemberAccessNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedMemberAccessNodeMatcher addMatcher(Matcher<TypedMemberAccessNode> matcher) {
        return new TypedMemberAccessNodeMatcher(matchers.plus(matcher));
    }
}
