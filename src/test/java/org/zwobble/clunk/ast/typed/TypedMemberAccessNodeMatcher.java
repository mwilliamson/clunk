package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.precisely.Matcher;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedMemberAccessNodeMatcher extends CastMatcher<Object, TypedMemberAccessNode> {
    private final PVector<Matcher<? super TypedMemberAccessNode>> matchers;

    public TypedMemberAccessNodeMatcher(PVector<Matcher<? super TypedMemberAccessNode>> matchers) {
        super(TypedMemberAccessNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedMemberAccessNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", x -> x.receiver(), receiver));
    }

    public TypedMemberAccessNodeMatcher withMemberName(Matcher<String> memberName) {
        return addMatcher(has("memberName", x -> x.memberName(), memberName));
    }

    public TypedMemberAccessNodeMatcher withType(Type type) {
        return addMatcher(has("type", x -> x.type(), equalTo(type)));
    }

    private TypedMemberAccessNodeMatcher addMatcher(Matcher<TypedMemberAccessNode> matcher) {
        return new TypedMemberAccessNodeMatcher(matchers.plus(matcher));
    }
}
