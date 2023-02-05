package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.matchers.HasMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedCallStaticFunctionNodeMatcher extends CastMatcher<Object, TypedCallStaticFunctionNode> {
    private final List<Matcher<? super TypedCallStaticFunctionNode>> matchers;

    public TypedCallStaticFunctionNodeMatcher(List<Matcher<? super TypedCallStaticFunctionNode>> matchers) {
        super(TypedCallStaticFunctionNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedCallStaticFunctionNodeMatcher withReceiver(Matcher<? super TypedExpressionNode> receiver) {
        return addMatcher(has("receiver", receiver));
    }

    public TypedCallStaticFunctionNodeMatcher withPositionalArgs(Matcher<Iterable<? extends TypedExpressionNode>> positionalArgs) {
        return addMatcher(HasMatcher.has(
            "positionalArgs",
            node -> node.args().positional(),
            positionalArgs
        ));
    }

    public TypedCallStaticFunctionNodeMatcher withType(Type type) {
        return addMatcher(has("type", equalTo(type)));
    }

    private TypedCallStaticFunctionNodeMatcher addMatcher(Matcher<TypedCallStaticFunctionNode> matcher) {
        return new TypedCallStaticFunctionNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
