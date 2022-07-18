package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class UntypedIfStatementNodeMatcher extends CastMatcher<Object, UntypedIfStatementNode> {
    private final List<Matcher<? super UntypedIfStatementNode>> matchers;

    public UntypedIfStatementNodeMatcher(List<Matcher<? super UntypedIfStatementNode>> matchers) {
        super(UntypedIfStatementNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedIfStatementNodeMatcher withConditionalBranches(
        Matcher<? extends Iterable<? extends UntypedConditionalBranchNode>> conditionalBranches
    ) {
        return addMatcher(has("conditionalBranches", conditionalBranches));
    }

    public UntypedIfStatementNodeMatcher withElseBody(Matcher<? extends Iterable<? extends UntypedFunctionStatementNode>> elseBody) {
        return addMatcher(has("elseBody", elseBody));
    }

    private UntypedIfStatementNodeMatcher addMatcher(Matcher<UntypedIfStatementNode> matcher) {
        return new UntypedIfStatementNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
