package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedCallStaticFunctionNodeMatcher extends CastMatcher<Object, TypedCallStaticFunctionNode> {
    private final List<Matcher<? super TypedCallStaticFunctionNode>> matchers;

    public TypedCallStaticFunctionNodeMatcher(List<Matcher<? super TypedCallStaticFunctionNode>> matchers) {
        super(TypedCallStaticFunctionNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedCallStaticFunctionNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", x -> x.receiver(), receiver));
    }

    public TypedCallStaticFunctionNodeMatcher withPositionalArgs(Matcher<Iterable<TypedExpressionNode>> positionalArgs) {
        return addMatcher(has(
            "positionalArgs",
            node -> node.args().positional(),
            positionalArgs
        ));
    }

    public TypedCallStaticFunctionNodeMatcher withNamedArgs(Matcher<Iterable<TypedNamedArgNode>> namedArgs) {
        return addMatcher(has(
            "namedArgs",
            node -> node.args().named(),
            namedArgs
        ));
    }

    public TypedCallStaticFunctionNodeMatcher withType(Type type) {
        return addMatcher(has("type", x -> x.type(), equalTo(type)));
    }

    private TypedCallStaticFunctionNodeMatcher addMatcher(Matcher<TypedCallStaticFunctionNode> matcher) {
        return new TypedCallStaticFunctionNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
