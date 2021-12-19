package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class UntypedRecordNodeMatcher extends CastMatcher<Object, UntypedRecordNode> {
    private final List<Matcher<? super UntypedRecordNode>> matchers;

    public UntypedRecordNodeMatcher(List<Matcher<? super UntypedRecordNode>> matchers) {
        super(UntypedRecordNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedRecordNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public UntypedRecordNodeMatcher withFields(
        Matcher<Iterable<? extends UntypedRecordFieldNode>> matcher
    ) {
        return addMatcher(has("fields", matcher));
    }

    private UntypedRecordNodeMatcher addMatcher(Matcher<UntypedRecordNode> matcher) {
        return new UntypedRecordNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
