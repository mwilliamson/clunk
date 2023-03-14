package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class UntypedParamNodeMatcher extends CastMatcher<Object, UntypedParamNode> {
    private final List<Matcher<? super UntypedParamNode>> matchers;

    public UntypedParamNodeMatcher(List<Matcher<? super UntypedParamNode>> matchers) {
        super(UntypedParamNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedParamNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public UntypedParamNodeMatcher withType(Matcher<UntypedTypeLevelExpressionNode> type) {
        return addMatcher(has("type", x -> x.type(), type));
    }

    private UntypedParamNodeMatcher addMatcher(Matcher<UntypedParamNode> matcher) {
        return new UntypedParamNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
