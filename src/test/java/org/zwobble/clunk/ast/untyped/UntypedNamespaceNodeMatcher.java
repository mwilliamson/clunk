package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.has;

public class UntypedNamespaceNodeMatcher extends CastMatcher<Object, UntypedNamespaceNode> {
    private final List<Matcher<? super UntypedNamespaceNode>> matchers;

    public UntypedNamespaceNodeMatcher(List<Matcher<? super UntypedNamespaceNode>> matchers) {
        super(UntypedNamespaceNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedNamespaceNodeMatcher withImports(Matcher<Iterable<UntypedImportNode>> imports) {
        return addMatcher(has("imports", x -> x.imports(), imports));
    }

    public UntypedNamespaceNodeMatcher withStatements(Matcher<Iterable<UntypedNamespaceStatementNode>> statements) {
        return addMatcher(has("statements", x -> x.statements(), statements));
    }

    private UntypedNamespaceNodeMatcher addMatcher(Matcher<UntypedNamespaceNode> matcher) {
        return new UntypedNamespaceNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
