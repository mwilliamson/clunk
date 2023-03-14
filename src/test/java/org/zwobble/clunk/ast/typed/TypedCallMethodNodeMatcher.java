package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;
import java.util.Optional;

import static org.zwobble.clunk.matchers.OptionalMatcher.present;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedCallMethodNodeMatcher extends CastMatcher<Object, TypedCallMethodNode> {
    private final List<Matcher<? super TypedCallMethodNode>> matchers;

    public TypedCallMethodNodeMatcher(List<Matcher<? super TypedCallMethodNode>> matchers) {
        super(TypedCallMethodNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedCallMethodNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", x -> x.receiver(), present(receiver)));
    }

    public TypedCallMethodNodeMatcher withImplicitReceiver() {
        return addMatcher(has("receiver", x -> x.receiver(), equalTo(Optional.empty())));
    }

    public TypedCallMethodNodeMatcher withMethodName(String methodName) {
        return addMatcher(has("methodName", x -> x.methodName(), equalTo(methodName)));
    }

    public TypedCallMethodNodeMatcher withPositionalArgs(Matcher<Iterable<TypedExpressionNode>> positionalArgs) {
        return addMatcher(has(
            "positionalArgs",
            node -> node.args().positional(),
            positionalArgs
        ));
    }

    public TypedCallMethodNodeMatcher withType(Type type) {
        return addMatcher(has("type", x -> x.type(), equalTo(type)));
    }

    private TypedCallMethodNodeMatcher addMatcher(Matcher<TypedCallMethodNode> matcher) {
        return new TypedCallMethodNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
