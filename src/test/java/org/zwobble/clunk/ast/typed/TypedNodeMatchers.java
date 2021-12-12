package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.types.Type;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypedNodeMatchers {
    @SafeVarargs
    public static Matcher<TypedArgNode> isTypedArgNode(Matcher<TypedArgNode>... matchers) {
        return allOf(matchers);
    }

    public static Matcher<TypedArgNode> typedArgNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<TypedArgNode> typedArgNodeHasType(Matcher<TypedStaticExpressionNode> type) {
        return has("type", type);
    }

    @SafeVarargs
    public static Matcher<TypedNamespaceStatementNode> isTypedFunctionNode(Matcher<TypedFunctionNode>... matchers) {
        return cast(TypedFunctionNode.class, matchers);
    }

    public static Matcher<TypedFunctionNode> typedFunctionNodeHasArgs(Matcher<? extends Iterable<? extends TypedArgNode>> args) {
        return has("args", args);
    }

    public static Matcher<TypedFunctionNode> typedFunctionNodeHasName(String name) {
        return has("name", equalTo(name));
    }

    public static Matcher<TypedFunctionNode> typedFunctionNodeHasReturnType(Matcher<TypedStaticExpressionNode> type) {
        return has("returnType", type);
    }

    public static Matcher<TypedNamespaceStatementNode> isTypedRecordNode(Matcher<TypedRecordNode> matcher) {
        return cast(TypedRecordNode.class, matcher);
    }

    public static Matcher<TypedStaticExpressionNode> isTypedStaticExpressionNode(Type type) {
        return has("type", equalTo(type));
    }
}
