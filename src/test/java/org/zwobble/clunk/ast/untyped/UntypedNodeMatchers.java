package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.util.P;
import org.zwobble.precisely.Matcher;

import java.util.List;
import java.util.Optional;

import static org.zwobble.precisely.Matchers.*;

public class UntypedNodeMatchers {
    public static Matcher<UntypedNode> isUntypedBlankLineNode() {
        return instanceOf(UntypedBlankLineNode.class);
    }

    public static Matcher<UntypedExpressionNode> isUntypedBoolLiteralNode(boolean value) {
        return instanceOf(UntypedBoolLiteralNode.class, has("value", x -> x.value(), equalTo(value)));
    }

    public static UntypedCallNodeMatcher isUntypedCallNode() {
        return new UntypedCallNodeMatcher(List.of());
    }

    public static Matcher<UntypedConditionalBranchNode> isUntypedConditionalBranchNode(
        Matcher<UntypedExpressionNode> condition,
        Matcher<Iterable<UntypedFunctionStatementNode>> body
    ) {
        return allOf(
            has("condition", x -> x.condition(), condition),
            has("body", x -> x.body(), body)
        );
    }

    public static Matcher<UntypedNamespaceStatementNode> isUntypedEnumNode(
        Matcher<String> name,
        Matcher<Iterable<String>> members
    ) {
        return instanceOf(
            UntypedEnumNode.class,
            has("name", x -> x.name(), name),
            has("members", x -> x.members(), members)
        );
    }

    public static Matcher<UntypedFunctionStatementNode> isUntypedExpressionStatementNode(Matcher<? super UntypedExpressionNode> expression) {
        return instanceOf(
            UntypedExpressionStatementNode.class,
            has("expression", x-> x.expression(), expression)
        );
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
        return instanceOf(
            UntypedImportNode.class,
            has("namespaceName", x -> x.namespaceName(), equalTo(namespaceName)),
            has("fieldName", x -> x.fieldName(), equalTo(fieldName))
        );
    }

    public static UntypedIndexNodeMatcher isUntypedIndexNode() {
        return new UntypedIndexNodeMatcher(P.vector());
    }

    public static Matcher<UntypedExpressionNode> isUntypedIntLiteralNode(int value) {
        return instanceOf(
            UntypedIntLiteralNode.class,
            has("value", x -> x.value(), equalTo(value))
        );
    }

    public static Matcher<UntypedExpressionNode> isUntypedLogicalNotNode(Matcher<UntypedExpressionNode> operand) {
        return instanceOf(
            UntypedLogicalNotNode.class,
            has("operand", x -> x.operand(), operand)
        );
    }

    public static Matcher<UntypedExpressionNode> isUntypedMapLiteralNode(
        Matcher<Iterable<UntypedMapEntryLiteralNode>> entries
    ) {
        return instanceOf(
            UntypedMapLiteralNode.class,
            has("entries", x -> x.entries(), entries)
        );
    }

    public static Matcher<UntypedMapEntryLiteralNode> isUntypedMapEntryLiteralNode(
        Matcher<UntypedExpressionNode> key,
        Matcher<UntypedExpressionNode> value
    ) {
        return allOf(
            has("key", x -> x.key(), key),
            has("value", x -> x.value(), value)
        );
    }

    public static Matcher<UntypedExpressionNode> isUntypedMemberAccessNode(
        Matcher<UntypedExpressionNode> receiver,
        Matcher<String> memberName
    ) {
        return instanceOf(
            UntypedMemberAccessNode.class,
            has("receiver", x -> x.receiver(), receiver),
            has("memberName", x -> x.memberName(), memberName)
        );
    }

    public static Matcher<UntypedExpressionNode> isUntypedMemberDefinitionReferenceNode(
        Matcher<UntypedExpressionNode> receiver,
        Matcher<String> memberName
    ) {
        return instanceOf(
            UntypedMemberDefinitionReferenceNode.class,
            has("receiver", x -> x.receiver(), receiver),
            has("memberName", x -> x.memberName(), memberName)
        );
    }

    public static Matcher<UntypedNamedArgNode> isUntypedNamedArg(
        String name,
        Matcher<UntypedExpressionNode> expression
    ) {
        return allOf(
            has("name", x -> x.name(), equalTo(name)),
            has("expression", x -> x.expression(), expression)
        );
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
        return instanceOf(
            UntypedReferenceNode.class,
            has("name", x -> x.name(), equalTo(name))
        );
    }

    public static UntypedReturnNodeMatcher isUntypedReturnNode() {
        return new UntypedReturnNodeMatcher(List.of());
    }

    public static Matcher<UntypedExpressionNode> isUntypedStringLiteralNode(String value) {
        return instanceOf(
            UntypedStringLiteralNode.class,
            has("value", x -> x.value(), equalTo(value))
        );
    }

    public static UntypedTestNodeMatcher isUntypedTestNode() {
        return new UntypedTestNodeMatcher(List.of());
    }

    public static UntypedTestSuiteNodeMatcher isUntypedTestSuiteNode() {
        return new UntypedTestSuiteNodeMatcher(List.of());
    }

    public static Matcher<UntypedTypeLevelExpressionNode> isUntypedTypeLevelReferenceNode(String value) {
        return instanceOf(
            UntypedTypeLevelReferenceNode.class,
            has("name", x -> x.name(), equalTo(value))
        );
    }

    public static Matcher<UntypedTypeLevelExpressionNode> isUntypedConstructedTypeNode(
        Matcher<UntypedTypeLevelExpressionNode> receiver,
        Matcher<Iterable<UntypedTypeLevelExpressionNode>> args
    ) {
        return instanceOf(
            UntypedConstructedTypeNode.class,
            has("receiver", x -> x.receiver(), receiver),
            has("args", x -> x.args(), args)
        );
    }

    public static UntypedVarNodeMatcher isUntypedVarNode() {
        return new UntypedVarNodeMatcher(List.of());
    }
}
