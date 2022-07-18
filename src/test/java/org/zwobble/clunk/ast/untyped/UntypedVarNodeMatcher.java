package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class UntypedVarNodeMatcher extends CastMatcher<Object, UntypedVarNode> {
    private final List<Matcher<? super UntypedVarNode>> matchers;

    public UntypedVarNodeMatcher(List<Matcher<? super UntypedVarNode>> matchers) {
        super(UntypedVarNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedVarNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public UntypedVarNodeMatcher withExpression(Matcher<UntypedExpressionNode> expression) {
        return addMatcher(has("expression", expression));
    }

    private UntypedVarNodeMatcher addMatcher(Matcher<UntypedVarNode> matcher) {
        return new UntypedVarNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
