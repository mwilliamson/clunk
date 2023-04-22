package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;
import java.util.Optional;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedCallConstructorNodeMatcher extends CastMatcher<Object, TypedCallConstructorNode> {
    private final List<Matcher<? super TypedCallConstructorNode>> matchers;

    public TypedCallConstructorNodeMatcher(List<Matcher<? super TypedCallConstructorNode>> matchers) {
        super(TypedCallConstructorNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedCallConstructorNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", x -> x.receiver(), receiver));
    }

    public TypedCallConstructorNodeMatcher withTypeArgs(Matcher<? super Optional<? extends Iterable<TypedTypeLevelExpressionNode>>> receiver) {
        return addMatcher(has("typeArgs", x -> x.typeArgs(), receiver));
    }

    public TypedCallConstructorNodeMatcher withPositionalArgs(Matcher<Iterable<TypedExpressionNode>> positionalArgs) {
        return addMatcher(has(
            "positionalArgs",
            node -> node.args().positional(),
            positionalArgs
        ));
    }

    public TypedCallConstructorNodeMatcher withType(Type type) {
        return addMatcher(has("type", x -> x.type(), equalTo(type)));
    }

    private TypedCallConstructorNodeMatcher addMatcher(Matcher<TypedCallConstructorNode> matcher) {
        return new TypedCallConstructorNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
