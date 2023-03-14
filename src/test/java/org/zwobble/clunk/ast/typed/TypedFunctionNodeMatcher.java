package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelExpressionNode;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedFunctionNodeMatcher extends CastMatcher<Object, TypedFunctionNode> {
    private final List<Matcher<? super TypedFunctionNode>> matchers;

    public TypedFunctionNodeMatcher(List<Matcher<? super TypedFunctionNode>> matchers) {
        super(TypedFunctionNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedFunctionNodeMatcher withPositionalParams(Matcher<Iterable<TypedParamNode>> positionalParams) {
        return addMatcher(has("positionalParams", x -> x.positionalParams(), positionalParams));
    }

    public TypedFunctionNodeMatcher withNamedParams(Matcher<Iterable<TypedParamNode>> namedParams) {
        return addMatcher(has("namedParams", x -> x.namedParams(), namedParams));
    }

    public TypedFunctionNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public TypedFunctionNodeMatcher withReturnType(Type type) {
        return addMatcher(has("returnType", x -> x.returnType(), isTypedTypeLevelExpressionNode(type)));
    }

    public TypedFunctionNodeMatcher withBody(Matcher<Iterable<TypedFunctionStatementNode>> body) {
        return addMatcher(has("body", x -> x.body(), body));
    }

    private TypedFunctionNodeMatcher addMatcher(Matcher<TypedFunctionNode> matcher) {
        return new TypedFunctionNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
