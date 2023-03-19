package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.precisely.Matcher;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedMemberDefinitionReferenceNodeMatcher extends CastMatcher<Object, TypedMemberDefinitionReferenceNode> {
    private final PVector<Matcher<? super TypedMemberDefinitionReferenceNode>> matchers;

    public TypedMemberDefinitionReferenceNodeMatcher(PVector<Matcher<? super TypedMemberDefinitionReferenceNode>> matchers) {
        super(TypedMemberDefinitionReferenceNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedMemberDefinitionReferenceNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("typeExpression", x -> x.typeExpression(), receiver));
    }

    public TypedMemberDefinitionReferenceNodeMatcher withMemberName(Matcher<String> memberName) {
        return addMatcher(has("memberName", x -> x.memberName(), memberName));
    }

    public TypedMemberDefinitionReferenceNodeMatcher withType(Type type) {
        return addMatcher(has("type", x -> x.type(), equalTo(type)));
    }

    private TypedMemberDefinitionReferenceNodeMatcher addMatcher(Matcher<TypedMemberDefinitionReferenceNode> matcher) {
        return new TypedMemberDefinitionReferenceNodeMatcher(matchers.plus(matcher));
    }
}
