package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedMemberReferenceNodeMatcher extends CastMatcher<Object, TypedMemberReferenceNode> {
    private final List<Matcher<? super TypedMemberReferenceNode>> matchers;

    public TypedMemberReferenceNodeMatcher(List<Matcher<? super TypedMemberReferenceNode>> matchers) {
        super(TypedMemberReferenceNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedMemberReferenceNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public TypedMemberReferenceNodeMatcher withType(Type type) {
        return addMatcher(has("type", x -> x.type(), equalTo(type)));
    }

    private TypedMemberReferenceNodeMatcher addMatcher(Matcher<TypedMemberReferenceNode> matcher) {
        return new TypedMemberReferenceNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
