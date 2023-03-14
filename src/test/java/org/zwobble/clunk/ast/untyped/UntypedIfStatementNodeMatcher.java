package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.has;

public class UntypedIfStatementNodeMatcher extends CastMatcher<Object, UntypedIfStatementNode> {
    private final List<Matcher<? super UntypedIfStatementNode>> matchers;

    public UntypedIfStatementNodeMatcher(List<Matcher<? super UntypedIfStatementNode>> matchers) {
        super(UntypedIfStatementNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedIfStatementNodeMatcher withConditionalBranches(
        Matcher<Iterable<UntypedConditionalBranchNode>> conditionalBranches
    ) {
        return addMatcher(has("conditionalBranches", x -> x.conditionalBranches(), conditionalBranches));
    }

    public UntypedIfStatementNodeMatcher withElseBody(Matcher<Iterable<UntypedFunctionStatementNode>> elseBody) {
        return addMatcher(has("elseBody", x -> x.elseBody(), elseBody));
    }

    private UntypedIfStatementNodeMatcher addMatcher(Matcher<UntypedIfStatementNode> matcher) {
        return new UntypedIfStatementNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
