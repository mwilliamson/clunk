package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;

import java.util.List;
import java.util.Optional;

public class Untyped {
    public static UntypedAddNode add(UntypedExpressionNode left, UntypedExpressionNode right) {
        return new UntypedAddNode(left, right, NullSource.INSTANCE);
    }

    public static UntypedBoolLiteralNode boolFalse() {
        return new UntypedBoolLiteralNode(false, NullSource.INSTANCE);
    }

    public static UntypedBoolLiteralNode boolTrue() {
        return new UntypedBoolLiteralNode(true, NullSource.INSTANCE);
    }

    public static UntypedCallNode call(
        UntypedExpressionNode receiver,
        List<UntypedTypeLevelExpressionNode> typeLevelArgs,
        List<UntypedExpressionNode> positionalArgs
    ) {
        return new UntypedCallNode(
            receiver,
            typeLevelArgs,
            new UntypedArgsNode(positionalArgs, List.of(), NullSource.INSTANCE),
            NullSource.INSTANCE
        );
    }

    public static UntypedCallNode call(
        UntypedExpressionNode receiver,
        List<UntypedExpressionNode> positionalArgs
    ) {
        return new UntypedCallNode(
            receiver,
            List.of(),
            new UntypedArgsNode(positionalArgs, List.of(), NullSource.INSTANCE),
            NullSource.INSTANCE
        );
    }

    public static UntypedCastUnsafeNode castUnsafe(
        UntypedExpressionNode expression,
        UntypedTypeLevelExpressionNode typeExpression
    ) {
        return new UntypedCastUnsafeNode(expression, typeExpression, NullSource.INSTANCE);
    }

    public static UntypedComprehensionIterableNode comprehensionIterable(
        String targetName,
        UntypedExpressionNode iterable
    ) {
        return new UntypedComprehensionIterableNode(targetName, iterable, List.of(), NullSource.INSTANCE);
    }

    public static UntypedConditionalBranchNode conditionalBranch(
        UntypedExpressionNode condition,
        List<UntypedFunctionStatementNode> body
    ) {
        return new UntypedConditionalBranchNode(condition, body, NullSource.INSTANCE);
    }

    public static UntypedConstructedTypeNode constructedType(
        UntypedTypeLevelReferenceNode receiver,
        List<UntypedTypeLevelExpressionNode> args
    ) {
        return new UntypedConstructedTypeNode(receiver, args, NullSource.INSTANCE);
    }

    public static UntypedEnumNode enum_(String name) {
        return new UntypedEnumNode(name, List.of(), NullSource.INSTANCE);
    }

    public static UntypedEqualsNode equals(UntypedExpressionNode left, UntypedExpressionNode right) {
        return new UntypedEqualsNode(left, right, NullSource.INSTANCE);
    }

    public static UntypedExpressionStatementNode expressionStatement(UntypedExpressionNode expression) {
        return new UntypedExpressionStatementNode(expression, NullSource.INSTANCE);
    }

    public static UntypedForEachNode forEach(
        String targetName,
        UntypedExpressionNode iterable,
        List<UntypedFunctionStatementNode> body
    ) {
        return new UntypedForEachNode(targetName, iterable, body, NullSource.INSTANCE);
    }

    public static UntypedIfStatementNode ifStatement(
        List<UntypedConditionalBranchNode> conditionalBranches
    ) {
        return ifStatement(conditionalBranches, List.of());
    }

    public static UntypedIfStatementNode ifStatement(
        List<UntypedConditionalBranchNode> conditionalBranches,
        List<UntypedFunctionStatementNode> elseBody
    ) {
        return new UntypedIfStatementNode(conditionalBranches, elseBody, NullSource.INSTANCE);
    }

    public static UntypedImportNode import_(NamespaceName name) {
        return new UntypedImportNode(name, Optional.empty(), NullSource.INSTANCE);
    }

    public static UntypedImportNode import_(NamespaceName name, String fieldName) {
        return new UntypedImportNode(name, Optional.of(fieldName), NullSource.INSTANCE);
    }

    public static UntypedIndexNode index(
        UntypedExpressionNode receiver,
        UntypedExpressionNode index
    ) {
        return new UntypedIndexNode(receiver, index, NullSource.INSTANCE);
    }

    public static UntypedInstanceOfNode instanceOf(
        UntypedExpressionNode expression,
        UntypedTypeLevelExpressionNode typeExpression
    ) {
        return new UntypedInstanceOfNode(expression, typeExpression, NullSource.INSTANCE);
    }

    public static UntypedInterfaceNode interface_(String name) {
        return new UntypedInterfaceNode(name, false, NullSource.INSTANCE);
    }

    public static UntypedInterfaceNode interfaceSealed(String name) {
        return new UntypedInterfaceNode(name, true, NullSource.INSTANCE);
    }

    public static UntypedInterfaceNode interfaceUnsealed(String name) {
        return new UntypedInterfaceNode(name, false, NullSource.INSTANCE);
    }

    public static UntypedIntLiteralNode intLiteral() {
        return intLiteral(0);
    }

    public static UntypedIntLiteralNode intLiteral(int value) {
        return new UntypedIntLiteralNode(value, NullSource.INSTANCE);
    }

    public static UntypedListComprehensionNode listComprehension(
        List<UntypedComprehensionIterableNode> iterables,
        UntypedExpressionNode yield
    ) {
        return new UntypedListComprehensionNode(iterables, yield, NullSource.INSTANCE);
    }

    public static UntypedExpressionNode memberAccess(UntypedExpressionNode receiver, String membername) {
        return new UntypedMemberAccessNode(receiver, membername, NullSource.INSTANCE, NullSource.INSTANCE);
    }

    public static UntypedExpressionNode memberDefinitionReference(UntypedExpressionNode typeExpression, String memberName) {
        return new UntypedMemberDefinitionReferenceNode(typeExpression, memberName, NullSource.INSTANCE, NullSource.INSTANCE);
    }

    public static UntypedListLiteralNode listLiteral(List<UntypedExpressionNode> elements) {
        return new UntypedListLiteralNode(elements, NullSource.INSTANCE);
    }

    public static UntypedLogicalAndNode logicalAnd(UntypedExpressionNode left, UntypedExpressionNode right) {
        return new UntypedLogicalAndNode(left, right, NullSource.INSTANCE);
    }

    public static UntypedLogicalNotNode logicalNot(UntypedExpressionNode operand) {
        return new UntypedLogicalNotNode(operand, NullSource.INSTANCE);
    }

    public static UntypedLogicalOrNode logicalOr(UntypedExpressionNode left, UntypedExpressionNode right) {
        return new UntypedLogicalOrNode(left, right, NullSource.INSTANCE);
    }

    public static UntypedMapLiteralNode mapLiteral(List<UntypedMapEntryLiteralNode> entries) {
        return new UntypedMapLiteralNode(entries, NullSource.INSTANCE);
    }

    public static UntypedMapEntryLiteralNode mapEntryLiteral(
        UntypedExpressionNode key,
        UntypedExpressionNode value
    ) {
        return new UntypedMapEntryLiteralNode(key, value, NullSource.INSTANCE);
    }

    public static UntypedNamedArgNode namedArg(String name, UntypedExpressionNode expression) {
        return new UntypedNamedArgNode(name, expression, NullSource.INSTANCE);
    }

    public static UntypedNotEqualNode notEqual(
        UntypedExpressionNode left,
        UntypedExpressionNode right
    ) {
        return new UntypedNotEqualNode(left, right, NullSource.INSTANCE);
    }

    public static UntypedParamNode param(String name, UntypedTypeLevelExpressionNode type) {
        return new UntypedParamNode(name, type, NullSource.INSTANCE);
    }

    public static UntypedPropertyNode property(
        String name,
        UntypedTypeLevelExpressionNode type,
        List<UntypedFunctionStatementNode> body
    ) {
        return new UntypedPropertyNode(name, type, body, NullSource.INSTANCE);
    }

    public static UntypedRecordFieldNode recordField(String name, UntypedTypeLevelExpressionNode type) {
        return new UntypedRecordFieldNode(name, type, NullSource.INSTANCE);
    }

    public static UntypedReferenceNode reference(String name) {
        return new UntypedReferenceNode(name, NullSource.INSTANCE);
    }

    public static UntypedReturnNode returnStatement() {
        return new UntypedReturnNode(boolFalse(), NullSource.INSTANCE);
    }

    public static UntypedReturnNode returnStatement(UntypedExpressionNode expression) {
        return new UntypedReturnNode(expression, NullSource.INSTANCE);
    }

    public static UntypedSingleLineCommentNode singleLineComment(String value) {
        return new UntypedSingleLineCommentNode(value, NullSource.INSTANCE);
    }

    public static UntypedStringLiteralNode string() {
        return string("");
    }

    public static UntypedStringLiteralNode string(String value) {
        return new UntypedStringLiteralNode(value, NullSource.INSTANCE);
    }

    public static UntypedSwitchCaseNode switchCase(
        UntypedTypeLevelExpressionNode type,
        List<UntypedFunctionStatementNode> body
    ) {
        return new UntypedSwitchCaseNode(type, body, NullSource.INSTANCE);
    }

    public static UntypedSwitchNode switchStatement(
        UntypedReferenceNode expression,
        List<UntypedSwitchCaseNode> cases
    ) {
        return new UntypedSwitchNode(expression, cases, NullSource.INSTANCE);
    }

    public static UntypedTypeLevelReferenceNode typeLevelReference(String value) {
        return new UntypedTypeLevelReferenceNode(value, NullSource.INSTANCE);
    }

    public static UntypedVarNode var(String name, UntypedExpressionNode expression) {
        return new UntypedVarNode(name, expression, NullSource.INSTANCE);
    }
}
