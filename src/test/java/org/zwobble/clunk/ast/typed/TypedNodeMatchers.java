package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.types.Type;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypedNodeMatchers {
    public static Matcher<TypedExpressionNode> isTypedBoolLiteral(boolean value) {
        return cast(TypedBoolLiteralNode.class, has("value", equalTo(value)));
    }

    @SafeVarargs
    public static Matcher<TypedNamespaceStatementNode> isTypedFunctionNode(Matcher<TypedFunctionNode>... matchers) {
        return cast(TypedFunctionNode.class, matchers);
    }

    public static Matcher<TypedFunctionNode> typedFunctionNodeHasParams(Matcher<? extends Iterable<? extends TypedParamNode>> params) {
        return has("params", params);
    }

    public static Matcher<TypedFunctionNode> typedFunctionNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<TypedFunctionNode> typedFunctionNodeHasReturnType(Matcher<TypedStaticExpressionNode> type) {
        return has("returnType", type);
    }

    @SafeVarargs
    public static Matcher<TypedParamNode> isTypedParamNode(Matcher<TypedParamNode>... matchers) {
        return allOf(matchers);
    }

    public static Matcher<TypedParamNode> typedParamNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<TypedParamNode> typedParamNodeHasType(Matcher<TypedStaticExpressionNode> type) {
        return has("type", type);
    }

    public static Matcher<TypedNamespaceStatementNode> isTypedRecordNode(Matcher<TypedRecordNode> matcher) {
        return cast(TypedRecordNode.class, matcher);
    }

    public static Matcher<TypedFunctionStatementNode> isTypedReturnNode(Matcher<TypedReturnNode> matcher) {
        return cast(TypedReturnNode.class, matcher);
    }

    public static Matcher<TypedReturnNode> typedReturnNodeHasExpression(Matcher<TypedExpressionNode> matcher) {
        return has("expression", matcher);
    }

    public static Matcher<TypedStaticExpressionNode> isTypedStaticExpressionNode(Type type) {
        return has("type", equalTo(type));
    }
}
