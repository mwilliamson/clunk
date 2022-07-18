package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class UntypedRecordFieldNodeMatcher extends CastMatcher<Object, UntypedRecordFieldNode> {
    private final List<Matcher<? super UntypedRecordFieldNode>> matchers;

    public UntypedRecordFieldNodeMatcher(List<Matcher<? super UntypedRecordFieldNode>> matchers) {
        super(UntypedRecordFieldNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public UntypedRecordFieldNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public UntypedRecordFieldNodeMatcher withType(Matcher<UntypedTypeLevelExpressionNode> type) {
        return addMatcher(has("type", type));
    }

    private UntypedRecordFieldNodeMatcher addMatcher(Matcher<UntypedRecordFieldNode> matcher) {
        return new UntypedRecordFieldNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
