package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.*;

public class UntypedVarNodeMatcher extends CastMatcher<Object, UntypedVarNode> {
    private final List<Matcher<? super UntypedVarNode>> matchers;

    public UntypedVarNodeMatcher(List<Matcher<? super UntypedVarNode>> matchers) {
        super(UntypedVarNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedVarNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public UntypedVarNodeMatcher withExpression(Matcher<UntypedExpressionNode> expression) {
        return addMatcher(has("expression", x -> x.expression(), expression));
    }

    private UntypedVarNodeMatcher addMatcher(Matcher<UntypedVarNode> matcher) {
        return new UntypedVarNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
