package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class UntypedFunctionNodeMatcher extends CastMatcher<Object, UntypedFunctionNode> {
    private final List<Matcher<? super UntypedFunctionNode>> matchers;

    public UntypedFunctionNodeMatcher(List<Matcher<? super UntypedFunctionNode>> matchers) {
        super(UntypedFunctionNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedFunctionNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public UntypedFunctionNodeMatcher withPositionalParams(Matcher<Iterable<UntypedParamNode>> params) {
        return addMatcher(has("positionalParams", x -> x.positionalParams(), params));
    }

    public UntypedFunctionNodeMatcher withNamedParams(Matcher<Iterable<UntypedParamNode>> params) {
        return addMatcher(has("namedParams", x -> x.namedParams(), params));
    }

    public UntypedFunctionNodeMatcher withReturnType(Matcher<UntypedTypeLevelExpressionNode> returnType) {
        return addMatcher(has("returnType", x -> x.returnType(), returnType));
    }

    public UntypedFunctionNodeMatcher withBody(Matcher<Iterable<UntypedFunctionStatementNode>> body) {
        return addMatcher(has("body", x -> x.body(), body));
    }

    private UntypedFunctionNodeMatcher addMatcher(Matcher<UntypedFunctionNode> matcher) {
        return new UntypedFunctionNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
