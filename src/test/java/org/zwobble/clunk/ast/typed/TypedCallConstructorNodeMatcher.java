package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedCallConstructorNodeMatcher extends CastMatcher<Object, TypedCallConstructorNode> {
    private final List<Matcher<? super TypedCallConstructorNode>> matchers;

    public TypedCallConstructorNodeMatcher(List<Matcher<? super TypedCallConstructorNode>> matchers) {
        super(TypedCallConstructorNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedCallConstructorNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", receiver));
    }

    public TypedCallConstructorNodeMatcher withTypeArgs(Matcher<Optional<Iterable<? extends TypedTypeLevelExpressionNode>>> receiver) {
        return addMatcher(has("typeArgs", receiver));
    }

    public TypedCallConstructorNodeMatcher withPositionalArgs(Matcher<? extends Iterable<? extends TypedExpressionNode>> positionalArgs) {
        return addMatcher(has("positionalArgs", positionalArgs));
    }

    public TypedCallConstructorNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedCallConstructorNodeMatcher addMatcher(Matcher<TypedCallConstructorNode> matcher) {
        return new TypedCallConstructorNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
