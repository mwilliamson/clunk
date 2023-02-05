package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelExpressionNode;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypedFunctionNodeMatcher extends CastMatcher<Object, TypedFunctionNode> {
    private final List<Matcher<? super TypedFunctionNode>> matchers;

    public TypedFunctionNodeMatcher(List<Matcher<? super TypedFunctionNode>> matchers) {
        super(TypedFunctionNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedFunctionNodeMatcher withPositionalParams(Matcher<? extends Iterable<? extends TypedParamNode>> positionalParams) {
        return addMatcher(has("positionalParams", positionalParams));
    }

    public TypedFunctionNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public TypedFunctionNodeMatcher withReturnType(Type type) {
        return addMatcher(has("returnType", isTypedTypeLevelExpressionNode(type)));
    }

    public TypedFunctionNodeMatcher withBody(Matcher<Iterable<? extends TypedFunctionStatementNode>> body) {
        return addMatcher(has("body", body));
    }

    private TypedFunctionNodeMatcher addMatcher(Matcher<TypedFunctionNode> matcher) {
        return new TypedFunctionNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
