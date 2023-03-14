package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.precisely.Matcher;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedIndexNodeMatcher extends CastMatcher<Object, TypedIndexNode> {
    private final PVector<Matcher<? super TypedIndexNode>> matchers;

    public TypedIndexNodeMatcher(PVector<Matcher<? super TypedIndexNode>> matchers) {
        super(TypedIndexNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedIndexNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", x -> x.receiver(), receiver));
    }

    public TypedIndexNodeMatcher withIndex(Matcher<? super TypedExpressionNode> index) {
        return addMatcher(has("index", x -> x.index(), index));
    }

    public TypedIndexNodeMatcher withType(Type type) {
        return addMatcher(has("type", x -> x.type(), equalTo(type)));
    }

    private TypedIndexNodeMatcher addMatcher(Matcher<TypedIndexNode> matcher) {
        return new TypedIndexNodeMatcher(matchers.plus(matcher));
    }
}
