package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.pcollections.PVector;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypedFieldAccessNodeMatcher extends CastMatcher<Object, TypedFieldAccessNode> {
    private final PVector<Matcher<? super TypedFieldAccessNode>> matchers;

    public TypedFieldAccessNodeMatcher(PVector<Matcher<? super TypedFieldAccessNode>> matchers) {
        super(TypedFieldAccessNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedFieldAccessNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", receiver));
    }

    public TypedFieldAccessNodeMatcher withFieldName(Matcher<? super String> fieldName) {
        return addMatcher(has("fieldName", fieldName));
    }

    public TypedFieldAccessNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedFieldAccessNodeMatcher addMatcher(Matcher<TypedFieldAccessNode> matcher) {
        return new TypedFieldAccessNodeMatcher(matchers.plus(matcher));
    }
}
