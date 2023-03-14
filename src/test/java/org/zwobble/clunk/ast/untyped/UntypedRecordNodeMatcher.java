package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class UntypedRecordNodeMatcher extends CastMatcher<Object, UntypedRecordNode> {
    private final List<Matcher<? super UntypedRecordNode>> matchers;

    public UntypedRecordNodeMatcher(List<Matcher<? super UntypedRecordNode>> matchers) {
        super(UntypedRecordNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedRecordNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public UntypedRecordNodeMatcher withFields(
        Matcher<Iterable<UntypedRecordFieldNode>> matcher
    ) {
        return addMatcher(has("fields", x -> x.fields(), matcher));
    }

    public UntypedRecordNodeMatcher withSupertypes(Matcher<Iterable<UntypedTypeLevelExpressionNode>> matcher) {
        return addMatcher(has("supertypes", x -> x.supertypes(), matcher));
    }

    public UntypedRecordNodeMatcher withBody(
        Matcher<Iterable<UntypedRecordBodyDeclarationNode>> matcher
    ) {
        return addMatcher(has("body", x -> x.body(), matcher));
    }

    private UntypedRecordNodeMatcher addMatcher(Matcher<UntypedRecordNode> matcher) {
        return new UntypedRecordNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
