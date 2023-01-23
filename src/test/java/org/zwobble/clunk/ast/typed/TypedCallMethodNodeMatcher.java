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
import static org.zwobble.clunk.matchers.OptionalMatcher.present;

public class TypedCallMethodNodeMatcher extends CastMatcher<Object, TypedCallMethodNode> {
    private final List<Matcher<? super TypedCallMethodNode>> matchers;

    public TypedCallMethodNodeMatcher(List<Matcher<? super TypedCallMethodNode>> matchers) {
        super(TypedCallMethodNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedCallMethodNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", present(receiver)));
    }

    public TypedCallMethodNodeMatcher withImplicitReceiver() {
        return addMatcher(has("receiver", equalTo(Optional.empty())));
    }

    public TypedCallMethodNodeMatcher withMethodName(String methodName) {
        return addMatcher(has("methodName", equalTo(methodName)));
    }

    public TypedCallMethodNodeMatcher withPositionalArgs(Matcher<? extends Iterable<? extends TypedExpressionNode>> positionalArgs) {
        return addMatcher(has("positionalArgs", positionalArgs));
    }

    public TypedCallMethodNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedCallMethodNodeMatcher addMatcher(Matcher<TypedCallMethodNode> matcher) {
        return new TypedCallMethodNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
