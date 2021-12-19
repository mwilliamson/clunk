package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedStaticExpressionNode;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypedParamNodeMatcher extends CastMatcher<Object, TypedParamNode> {
    private final List<Matcher<? super TypedParamNode>> matchers;

    public TypedParamNodeMatcher(List<Matcher<? super TypedParamNode>> matchers) {
        super(TypedParamNode.class, allOf(matchers));
        this.matchers = matchers;
    }

    public TypedParamNodeMatcher withName(String name) {
        return addMatcher(has("name", equalTo(name)));
    }

    public TypedParamNodeMatcher withType(Type type) {
        return addMatcher(has("type", isTypedStaticExpressionNode(type)));
    }

    private TypedParamNodeMatcher addMatcher(Matcher<TypedParamNode> matcher) {
        return new TypedParamNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
