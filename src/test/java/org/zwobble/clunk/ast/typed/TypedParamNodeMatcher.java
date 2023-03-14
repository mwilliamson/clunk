package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.matchers.CastMatcher;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.Lists;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelExpressionNode;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.has;

public class TypedParamNodeMatcher extends CastMatcher<Object, TypedParamNode> {
    private final List<Matcher<? super TypedParamNode>> matchers;

    public TypedParamNodeMatcher(List<Matcher<? super TypedParamNode>> matchers) {
        super(TypedParamNode.class, matchers);
        this.matchers = matchers;
    }

    public TypedParamNodeMatcher withName(String name) {
        return addMatcher(has("name", x -> x.name(), equalTo(name)));
    }

    public TypedParamNodeMatcher withType(Type type) {
        return addMatcher(has("type", x -> x.type(), isTypedTypeLevelExpressionNode(type)));
    }

    private TypedParamNodeMatcher addMatcher(Matcher<TypedParamNode> matcher) {
        return new TypedParamNodeMatcher(Lists.concatOne(matchers, matcher));
    }
}
