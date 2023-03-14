package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class UntypedRecordFieldNodeMatcher extends CastMatcher<Object, UntypedRecordFieldNode> {
    private final List<Matcher<? super UntypedRecordFieldNode>> matchers;

    public UntypedRecordFieldNodeMatcher(List<Matcher<? super UntypedRecordFieldNode>> matchers) {
        super(UntypedRecordFieldNode.class, matchers);
        this.matchers = matchers;
    }

    public UntypedRecordFieldNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public UntypedRecordFieldNodeMatcher withType(Matcher<UntypedTypeLevelExpressionNode> type) {
        return addMatcher(has("type", x -> x.type(), type));
    }

    private UntypedRecordFieldNodeMatcher addMatcher(Matcher<UntypedRecordFieldNode> matcher) {
        return new UntypedRecordFieldNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
