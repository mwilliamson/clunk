package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedMemberReferenceNodeMatcher extends CastMatcher<Object, TypedMemberReferenceNode> {
    private final List<Matcher<? super TypedMemberReferenceNode>> matchers;

    public TypedMemberReferenceNodeMatcher(List<Matcher<? super TypedMemberReferenceNode>> matchers) {
        super(TypedMemberReferenceNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedMemberReferenceNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public TypedMemberReferenceNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedMemberReferenceNodeMatcher addMatcher(Matcher<TypedMemberReferenceNode> matcher) {
        return new TypedMemberReferenceNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
