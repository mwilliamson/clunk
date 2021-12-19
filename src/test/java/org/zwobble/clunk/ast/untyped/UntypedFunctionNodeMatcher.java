package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class UntypedFunctionNodeMatcher extends CastMatcher<Object, UntypedFunctionNode> {
    private final List<Matcher<? super UntypedFunctionNode>> matchers;

    public UntypedFunctionNodeMatcher(List<Matcher<? super UntypedFunctionNode>> matchers) {
        super(UntypedFunctionNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedFunctionNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public UntypedFunctionNodeMatcher withParams(Matcher<? extends Iterable<? extends UntypedParamNode>> params) {
        return addMatcher(has("params", params));
    }

    public UntypedFunctionNodeMatcher withReturnType(Matcher<UntypedStaticExpressionNode> returnType) {
        return addMatcher(has("returnType", returnType));
    }

    public UntypedFunctionNodeMatcher withBody(Matcher<? extends Iterable<? extends UntypedFunctionStatementNode>> body) {
        return addMatcher(has("body", body));
    }

    private UntypedFunctionNodeMatcher addMatcher(Matcher<UntypedFunctionNode> matcher) {
        return new UntypedFunctionNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
