package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class UntypedNamespaceNodeMatcher extends CastMatcher<Object, UntypedNamespaceNode> {
    private final List<Matcher<? super UntypedNamespaceNode>> matchers;

    public UntypedNamespaceNodeMatcher(List<Matcher<? super UntypedNamespaceNode>> matchers) {
        super(UntypedNamespaceNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedNamespaceNodeMatcher withImports(Matcher<Iterable<? extends UntypedImportNode>> imports) {
        return addMatcher(has("imports", imports));
    }

    public UntypedNamespaceNodeMatcher withStatements(Matcher<Iterable<? extends UntypedNamespaceStatementNode>> statements) {
        return addMatcher(has("statements", statements));
    }

    private UntypedNamespaceNodeMatcher addMatcher(Matcher<UntypedNamespaceNode> matcher) {
        return new UntypedNamespaceNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
