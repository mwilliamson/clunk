package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedReferenceNodeMatcher  extends CastMatcher<Object, TypedLocalReferenceNode> {
    private final List<Matcher<? super TypedLocalReferenceNode>> matchers;

    public TypedReferenceNodeMatcher(List<Matcher<? super TypedLocalReferenceNode>> matchers) {
        super(TypedLocalReferenceNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedReferenceNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public TypedReferenceNodeMatcher withType(Type type) {
        return addMatcher(has("type", x -> x.type(), equalTo(type)));
    }

    private TypedReferenceNodeMatcher addMatcher(Matcher<TypedLocalReferenceNode> matcher) {
        return new TypedReferenceNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
