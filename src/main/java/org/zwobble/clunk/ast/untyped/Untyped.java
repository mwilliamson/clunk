package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;

import java.util.List;
import java.util.Optional;

public class Untyped {
    public static UntypedBoolLiteralNode boolFalse() {
        return new UntypedBoolLiteralNode(false, NullSource.INSTANCE);
    }

    public static UntypedBoolLiteralNode boolTrue() {
        return new UntypedBoolLiteralNode(true, NullSource.INSTANCE);
    }

    public static UntypedCallNode call(
        UntypedExpressionNode receiver,
        List<UntypedExpressionNode> positionalArgs
    ) {
        return new UntypedCallNode(receiver, positionalArgs, NullSource.INSTANCE);
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

    public static UntypedExpressionStatementNode expressionStatement(UntypedExpressionNode expression) {
        return new UntypedExpressionStatementNode(expression, NullSource.INSTANCE);
    }

    public static UntypedExpressionNode fieldAccess(UntypedExpressionNode receiver, String fieldName) {
        return new UntypedFieldAccessNode(receiver, fieldName, NullSource.INSTANCE);
    }

    public static UntypedIfStatementNode ifStatement(
        List<UntypedConditionalBranchNode> conditionalBranches,
        List<UntypedFunctionStatementNode> elseBody
    ) {
        return new UntypedIfStatementNode(conditionalBranches, elseBody, NullSource.INSTANCE);
    }

    public static UntypedImportNode import_(NamespaceName name, String fieldName) {
        return new UntypedImportNode(name, Optional.of(fieldName), NullSource.INSTANCE);
    }

    public static UntypedInterfaceNode interface_(String name) {
        return new UntypedInterfaceNode(name, NullSource.INSTANCE);
    }

    public static UntypedIntLiteralNode intLiteral() {
        return intLiteral(0);
    }

    public static UntypedIntLiteralNode intLiteral(int value) {
        return new UntypedIntLiteralNode(value, NullSource.INSTANCE);
    }

    public static UntypedParamNode param(String name, UntypedTypeLevelExpressionNode type) {
        return new UntypedParamNode(name, type, NullSource.INSTANCE);
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

    public static UntypedTypeLevelReferenceNode staticReference(String value) {
        return new UntypedTypeLevelReferenceNode(value, NullSource.INSTANCE);
    }

    public static UntypedStringLiteralNode string(String value) {
        return new UntypedStringLiteralNode(value, NullSource.INSTANCE);
    }

    public static UntypedVarNode var(String name, UntypedExpressionNode expression) {
        return new UntypedVarNode(name, expression, NullSource.INSTANCE);
    }
}
