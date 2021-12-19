package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class UntypedParamNodeMatcher extends CastMatcher<Object, UntypedParamNode> {
    private final List<Matcher<? super UntypedParamNode>> matchers;

    public UntypedParamNodeMatcher(List<Matcher<? super UntypedParamNode>> matchers) {
        super(UntypedParamNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedParamNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public UntypedParamNodeMatcher withType(Matcher<UntypedStaticExpressionNode> type) {
        return addMatcher(has("type", type));
    }

    private UntypedParamNodeMatcher addMatcher(Matcher<UntypedParamNode> matcher) {
        return new UntypedParamNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
