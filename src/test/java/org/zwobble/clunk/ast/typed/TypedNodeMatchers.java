package org.zwobble.clunk.ast.typed;

import org.hamcrest.Matcher;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.Type;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypedNodeMatchers {
    public static Matcher<TypedExpressionNode> isTypedBoolLiteralNode(boolean value) {
        return cast(TypedBoolLiteralNode.class, has("value", equalTo(value)));
    }

    public static TypedCallNodeMatcher isTypedCallNode() {
        return new TypedCallNodeMatcher(List.of());
    }

    public static TypedFunctionNodeMatcher isTypedFunctionNode() {
        return new TypedFunctionNodeMatcher(List.of());
    }

    public static Matcher<TypedExpressionNode> isTypedIntLiteralNode(int value) {
        return cast(TypedIntLiteralNode.class, has("value", equalTo(value)));
    }

    public static TypedParamNodeMatcher isTypedParamNode() {
        return new TypedParamNodeMatcher(List.of());
    }

    public static Matcher<TypedReceiverStaticFunctionNode> isTypedReceiverStaticFunctionNode(
        NamespaceType namespaceType,
        String functionName
    ) {
        return allOf(
            has("namespaceType", equalTo(namespaceType)),
            has("functionName", equalTo(functionName))
        );
    }

    public static Matcher<TypedNamespaceStatementNode> isTypedRecordNode(Matcher<TypedRecordNode> matcher) {
        return cast(TypedRecordNode.class, matcher);
    }

    public static TypedReferenceNodeMatcher isTypedReferenceNode() {
        return new TypedReferenceNodeMatcher(List.of());
    }

    public static TypedReturnNodeMatcher isTypedReturnNode() {
        return new TypedReturnNodeMatcher(List.of());
    }

    public static Matcher<TypedStaticExpressionNode> isTypedStaticExpressionNode(Type type) {
        return has("type", equalTo(type));
    }

    public static TypedTestNodeMatcher isTypedTestNode() {
        return new TypedTestNodeMatcher(List.of());
    }

    public static TypedVarNodeMatcher isTypedVarNode() {
        return new TypedVarNodeMatcher(List.of());
    }
}
