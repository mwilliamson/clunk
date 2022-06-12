package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class UntypedIfStatementNodeMatcher extends CastMatcher<Object, UntypedIfStatementNode> {
    private final List<Matcher<? super UntypedIfStatementNode>> matchers;

    public UntypedIfStatementNodeMatcher(List<Matcher<? super UntypedIfStatementNode>> matchers) {
        super(UntypedIfStatementNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedIfStatementNodeMatcher withConditionalBranches(
        Matcher<? extends Iterable<? extends UntypedIfStatementNode.ConditionalBranch>> conditionalBranches
    ) {
        return addMatcher(has("conditionalBranches", conditionalBranches));
    }

    public UntypedIfStatementNodeMatcher withElseBranch(Matcher<? extends Iterable<? extends UntypedFunctionStatementNode>> elseBranch) {
        return addMatcher(has("elseBranch", elseBranch));
    }

    private UntypedIfStatementNodeMatcher addMatcher(Matcher<UntypedIfStatementNode> matcher) {
        return new UntypedIfStatementNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
