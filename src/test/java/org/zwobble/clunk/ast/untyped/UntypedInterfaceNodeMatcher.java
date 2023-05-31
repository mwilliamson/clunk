package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class UntypedInterfaceNodeMatcher extends CastMatcher<Object, UntypedInterfaceNode> {
    private final List<Matcher<? super UntypedInterfaceNode>> matchers;

    public UntypedInterfaceNodeMatcher(List<Matcher<? super UntypedInterfaceNode>> matchers) {
        super(UntypedInterfaceNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedInterfaceNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public UntypedInterfaceNodeMatcher withBody(
        Matcher<Iterable<UntypedInterfaceBodyDeclarationNode>> matcher
    ) {
        return addMatcher(has("body", x -> x.body(), matcher));
    }

    private UntypedInterfaceNodeMatcher addMatcher(Matcher<UntypedInterfaceNode> matcher) {
        return new UntypedInterfaceNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
