package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypedReferenceNodeMatcher  extends CastMatcher<Object, TypedReferenceNode> {
    private final List<Matcher<? super TypedReferenceNode>> matchers;

    public TypedReferenceNodeMatcher(List<Matcher<? super TypedReferenceNode>> matchers) {
        super(TypedReferenceNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedReferenceNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public TypedReferenceNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedReferenceNodeMatcher addMatcher(Matcher<TypedReferenceNode> matcher) {
        return new TypedReferenceNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
