package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedReferenceNodeMatcher  extends CastMatcher<Object, TypedLocalReferenceNode> {
    private final List<Matcher<? super TypedLocalReferenceNode>> matchers;

    public TypedReferenceNodeMatcher(List<Matcher<? super TypedLocalReferenceNode>> matchers) {
        super(TypedLocalReferenceNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedReferenceNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public TypedReferenceNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedReferenceNodeMatcher addMatcher(Matcher<TypedLocalReferenceNode> matcher) {
        return new TypedReferenceNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
