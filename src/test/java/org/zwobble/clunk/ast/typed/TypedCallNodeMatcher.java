package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypedCallNodeMatcher extends CastMatcher<Object, TypedCallNode> {
    private final List<Matcher<? super TypedCallNode>> matchers;

    public TypedCallNodeMatcher(List<Matcher<? super TypedCallNode>> matchers) {
        super(TypedCallNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedCallNodeMatcher withReceiver(Matcher<TypedReceiverStaticFunctionNode> receiver) {
        return addMatcher(has("receiver", receiver));
    }

    public TypedCallNodeMatcher withPositionalArgs(Matcher<? extends Iterable<? extends TypedExpressionNode>> positionalArgs) {
        return addMatcher(has("positionalArgs", positionalArgs));
    }

    public TypedCallNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedCallNodeMatcher addMatcher(Matcher<TypedCallNode> matcher) {
        return new TypedCallNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
