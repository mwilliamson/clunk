package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.StructuredType;
import org.zwobble.clunk.types.TypeLevelValue;
import org.zwobble.clunk.util.P;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.*;

public class TypedNodeMatchers {
    public static Matcher<TypedExpressionNode> isTypedBoolLiteralNode(boolean value) {
        return instanceOf(TypedBoolLiteralNode.class, has("value", x -> x.value(), equalTo(value)));
    }

    public static TypedCallConstructorNodeMatcher isTypedCallConstructorNode() {
        return new TypedCallConstructorNodeMatcher(List.of());
    }

    public static TypedCallMethodNodeMatcher isTypedCallMethodNode() {
        return new TypedCallMethodNodeMatcher(List.of());
    }

    public static TypedCallStaticFunctionNodeMatcher isTypedCallStaticFunctionNode() {
        return new TypedCallStaticFunctionNodeMatcher(List.of());
    }

    public static Matcher<TypedFunctionStatementNode> isTypedExpressionStatementNode(Matcher<? super TypedExpressionNode> expression) {
        return instanceOf(TypedExpressionStatementNode.class, has("expression", x -> x.expression(), expression));
    }

    public static TypedFunctionNodeMatcher isTypedFunctionNode() {
        return new TypedFunctionNodeMatcher(List.of());
    }

    public static Matcher<TypedImportNode> isTypedImportNode(Matcher<TypedImportNode> matcher) {
        return matcher;
    }

    public static TypedIndexNodeMatcher isTypedIndexNode() {
        return new TypedIndexNodeMatcher(P.vector());
    }

    public static Matcher<TypedExpressionNode> isTypedIntLiteralNode(int value) {
        return instanceOf(TypedIntLiteralNode.class, has("value", x -> x.value(), equalTo(value)));
    }

    public static Matcher<TypedMapEntryLiteralNode> isTypedMapEntryLiteralNode(
        Matcher<TypedExpressionNode> key,
        Matcher<TypedExpressionNode> value
    ) {
        return allOf(
            has("key", x -> x.key(), key),
            has("value", x -> x.value(), value)
        );
    }

    public static TypedMemberAccessNodeMatcher isTypedMemberAccessNode() {
        return new TypedMemberAccessNodeMatcher(P.vector());
    }

    public static TypedMemberDefinitionReferenceNodeMatcher isTypedMemberDefinitionReferenceNode() {
        return new TypedMemberDefinitionReferenceNodeMatcher(P.vector());
    }

    public static TypedMemberReferenceNodeMatcher isTypedMemberReferenceNode() {
        return new TypedMemberReferenceNodeMatcher(List.of());
    }

    public static Matcher<TypedNamedArgNode> isTypedNamedArgNode(
        String name,
        Matcher<TypedExpressionNode> expression
    ) {
        return allOf(
            has("name", x -> x.name(), equalTo(name)),
            has("expression", x -> x.expression(), expression)
        );
    }

    public static TypedParamNodeMatcher isTypedParamNode() {
        return new TypedParamNodeMatcher(List.of());
    }

    public static Matcher<TypedReceiverStaticFunctionNode> isTypedReceiverStaticFunctionNode(
        NamespaceType namespaceType,
        String functionName
    ) {
        return allOf(
            has("namespaceType", x -> x.namespaceType(), equalTo(namespaceType)),
            has("functionName", x -> x.functionName(), equalTo(functionName))
        );
    }

    public static Matcher<TypedNamespaceStatementNode> isTypedRecordNode(Matcher<TypedRecordNode> matcher) {
        return instanceOf(TypedRecordNode.class, matcher);
    }

    public static TypedReferenceNodeMatcher isTypedReferenceNode() {
        return new TypedReferenceNodeMatcher(List.of());
    }

    public static TypedReturnNodeMatcher isTypedReturnNode() {
        return new TypedReturnNodeMatcher(List.of());
    }

    public static Matcher<TypedExpressionNode> isTypedStringLiteralNode(String value) {
        return instanceOf(TypedStringLiteralNode.class, has("value", x -> x.value(), equalTo(value)));
    }

    public static TypedTestNodeMatcher isTypedTestNode() {
        return new TypedTestNodeMatcher(List.of());
    }

    public static TypedTestSuiteNodeMatcher isTypedTestSuiteNode() {
        return new TypedTestSuiteNodeMatcher(List.of());
    }

    public static Matcher<TypedTypeLevelExpressionNode> isTypedTypeLevelExpressionNode(TypeLevelValue value) {
        return has("value", x -> x.value(), equalTo(value));
    }

    public static Matcher<TypedTypeLevelExpressionNode> isTypedTypeLevelReferenceNode(String name, TypeLevelValue value) {
        return instanceOf(
            TypedTypeLevelReferenceNode.class,
            has("name", x -> x.name(), equalTo(name)),
            has("value", x -> x.value(), equalTo(value))
        );
    }

    public static Matcher<TypedFunctionStatementNode> isTypedTypeNarrowNode(
        String variableName,
        Matcher<StructuredType> type
    ) {
        return instanceOf(
            TypedTypeNarrowNode.class,
            has("variableName", x -> x.variableName(), equalTo(variableName)),
            has("type", x -> x.type(), type)
        );
    }

    public static TypedVarNodeMatcher isTypedVarNode() {
        return new TypedVarNodeMatcher(List.of());
    }
}
