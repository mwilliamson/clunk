package org.zwobble.clunk.ast.untyped;

import org.hamcrest.Matcher;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.util.P;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class UntypedNodeMatchers {
    public static Matcher<UntypedNode> isUntypedBlankLineNode() {
        return cast(UntypedBlankLineNode.class);
    }

    public static Matcher<UntypedExpressionNode> isUntypedBoolLiteralNode(boolean value) {
        return cast(UntypedBoolLiteralNode.class, has("value", equalTo(value)));
    }

    public static UntypedCallNodeMatcher isUntypedCallNode() {
        return new UntypedCallNodeMatcher(List.of());
    }

    public static Matcher<UntypedConditionalBranchNode> isUntypedConditionalBranchNode(
        Matcher<UntypedExpressionNode> condition,
        Matcher<? extends Iterable<? extends UntypedFunctionStatementNode>> body
    ) {
        return allOf(
            has("condition", condition),
            has("body", body)
        );
    }

    public static Matcher<UntypedNamespaceStatementNode> isUntypedEnumNode(
        Matcher<String> name,
        Matcher<? extends Iterable<? extends String>> members
    ) {
        return cast(
            UntypedEnumNode.class,
            has("name", name),
            has("members", members)
        );
    }

    public static Matcher<UntypedFunctionStatementNode> isUntypedExpressionStatementNode(Matcher<UntypedExpressionNode> expression) {
        return cast(UntypedExpressionStatementNode.class, has("expression", expression));
    }

    public static UntypedFunctionNodeMatcher isUntypedFunctionNode() {
        return new UntypedFunctionNodeMatcher(List.of());
    }

    public static UntypedIfStatementNodeMatcher isUntypedIfStatementNode() {
        return new UntypedIfStatementNodeMatcher(List.of());
    }

    public static Matcher<UntypedImportNode> isUntypedImportNode(
        NamespaceName namespaceName,
        Optional<String> fieldName
    ) {
        return cast(UntypedImportNode.class, allOf(
            has("namespaceName", equalTo(namespaceName)),
            has("fieldName", equalTo(fieldName))
        ));
    }

    public static UntypedIndexNodeMatcher isUntypedIndexNode() {
        return new UntypedIndexNodeMatcher(P.vector());
    }

    public static Matcher<UntypedExpressionNode> isUntypedIntLiteralNode(int value) {
        return cast(UntypedIntLiteralNode.class, has("value", equalTo(value)));
    }

    public static Matcher<UntypedExpressionNode> isUntypedLogicalNotNode(Matcher<UntypedExpressionNode> operand) {
        return cast(UntypedLogicalNotNode.class, has("operand", operand));
    }

    public static Matcher<UntypedExpressionNode> isUntypedMemberAccessNode(
        Matcher<UntypedExpressionNode> receiver,
        Matcher<String> memberName
    ) {
        return cast(UntypedMemberAccessNode.class, allOf(
            has("receiver", receiver),
            has("memberName", memberName)
        ));
    }

    public static UntypedNamespaceNodeMatcher isUntypedNamespaceNode() {
        return new UntypedNamespaceNodeMatcher(List.of());
    }

    public static UntypedParamNodeMatcher isUntypedParamNode() {
        return new UntypedParamNodeMatcher(List.of());
    }

    public static UntypedRecordNodeMatcher isUntypedRecordNode() {
        return new UntypedRecordNodeMatcher(List.of());
    }

    public static UntypedRecordFieldNodeMatcher isUntypedRecordFieldNode() {
        return new UntypedRecordFieldNodeMatcher(List.of());
    }

    public static Matcher<UntypedExpressionNode> isUntypedReferenceNode(String name) {
        return cast(UntypedReferenceNode.class, has("name", equalTo(name)));
    }

    public static UntypedReturnNodeMatcher isUntypedReturnNode() {
        return new UntypedReturnNodeMatcher(List.of());
    }

    public static Matcher<UntypedExpressionNode> isUntypedStringLiteralNode(String value) {
        return cast(UntypedStringLiteralNode.class, has("value", equalTo(value)));
    }

    public static UntypedTestNodeMatcher isUntypedTestNode() {
        return new UntypedTestNodeMatcher(List.of());
    }

    public static UntypedTestSuiteNodeMatcher isUntypedTestSuiteNode() {
        return new UntypedTestSuiteNodeMatcher(List.of());
    }

    public static Matcher<UntypedTypeLevelExpressionNode> isUntypedTypeLevelReferenceNode(String value) {
        return cast(UntypedTypeLevelExpressionNode.class, has("name", equalTo(value)));
    }

    public static Matcher<UntypedTypeLevelExpressionNode> isUntypedConstructedTypeNode(
        Matcher<UntypedTypeLevelExpressionNode> receiver,
        Matcher<? extends Iterable<? extends UntypedTypeLevelExpressionNode>> args
    ) {
        return cast(UntypedConstructedTypeNode.class, allOf(
            has("receiver", receiver),
            has("args", args)
        ));
    }

    public static UntypedVarNodeMatcher isUntypedVarNode() {
        return new UntypedVarNodeMatcher(List.of());
    }
}
