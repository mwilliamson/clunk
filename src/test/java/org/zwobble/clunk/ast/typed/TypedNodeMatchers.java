package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.types.Type;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypedNodeMatchers {
    public static Matcher<TypedExpressionNode> isTypedBoolLiteral(boolean value) {
        return cast(TypedBoolLiteralNode.class, has("value", equalTo(value)));
    }

    public static TypedFunctionNodeMatcher isTypedFunctionNode() {
        return new TypedFunctionNodeMatcher(List.of());
    }

    public static TypedParamNodeMatcher isTypedParamNode() {
        return new TypedParamNodeMatcher(List.of());
    }

    public static Matcher<TypedNamespaceStatementNode> isTypedRecordNode(Matcher<TypedRecordNode> matcher) {
        return cast(TypedRecordNode.class, matcher);
    }

    public static TypedReturnNodeMatcher isTypedReturnNode() {
        return new TypedReturnNodeMatcher(List.of());
    }

    public static Matcher<TypedStaticExpressionNode> isTypedStaticExpressionNode(Type type) {
        return has("type", equalTo(type));
    }
}
