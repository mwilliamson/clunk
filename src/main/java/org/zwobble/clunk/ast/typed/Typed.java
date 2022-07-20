package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Optional;

public class Typed {
    public static TypedBoolLiteralNode boolFalse() {
        return new TypedBoolLiteralNode(false, NullSource.INSTANCE);
    }

    public static TypedBoolLiteralNode boolTrue() {
        return new TypedBoolLiteralNode(true, NullSource.INSTANCE);
    }

    public static TypedCallNode call(TypedExpressionNode receiver, List<TypedExpressionNode> args, Type type) {
        return new TypedCallNode(receiver, args, type, NullSource.INSTANCE);
    }

    public static TypedConditionalBranchNode conditionalBranch(
        TypedExpressionNode condition,
        List<TypedFunctionStatementNode> body
    ) {
        return new TypedConditionalBranchNode(condition, body, NullSource.INSTANCE);
    }

    public static TypedConstructedTypeNode constructedType(
        TypedTypeLevelExpressionNode receiver,
        List<TypedTypeLevelExpressionNode> args,
        TypeLevelValue value
    ) {
        return new TypedConstructedTypeNode(receiver, args, value, NullSource.INSTANCE);
    }

    public static TypedExpressionStatementNode expressionStatement(TypedExpressionNode expression) {
        return new TypedExpressionStatementNode(expression, NullSource.INSTANCE);
    }

    public static TypedIfStatementNode ifStatement(
        List<TypedConditionalBranchNode> conditionalBranches,
        List<TypedFunctionStatementNode> elseBody
    ) {
        return new TypedIfStatementNode(conditionalBranches, elseBody, NullSource.INSTANCE);
    }

    public static TypedImportNode import_(NamespaceName name, Type type) {
        return new TypedImportNode(name, Optional.empty(), type, NullSource.INSTANCE);
    }

    public static TypedImportNode import_(NamespaceName name, String fieldName, Type type) {
        return new TypedImportNode(name, Optional.of(fieldName), type, NullSource.INSTANCE);
    }

    public static TypedInterfaceNode interface_(String name, InterfaceType type) {
        return new TypedInterfaceNode(name, type, NullSource.INSTANCE);
    }

    public static TypedIntAddNode intAdd(TypedExpressionNode left, TypedExpressionNode right) {
        return new TypedIntAddNode(left, right, NullSource.INSTANCE);
    }

    public static TypedExpressionNode intLiteral(int value) {
        return new TypedIntLiteralNode(value, NullSource.INSTANCE);
    }

    public static TypedParamNode param(String name, TypedTypeLevelExpressionNode type) {
        return new TypedParamNode(name, type, NullSource.INSTANCE);
    }

    public static TypedPropertyNode property(
        String name,
        TypedTypeLevelExpressionNode type,
        List<TypedFunctionStatementNode> body
    ) {
        return new TypedPropertyNode(name, type, body, NullSource.INSTANCE);
    }

    public static TypedRecordFieldNode recordField(String name, TypedTypeLevelExpressionNode type) {
        return new TypedRecordFieldNode(name, type, NullSource.INSTANCE);
    }

    public static TypedReferenceNode referenceVariable(String name, Type type) {
        return TypedReferenceNode.variable(name, type, NullSource.INSTANCE);
    }

    public static TypedReturnNode returnStatement(TypedExpressionNode expression) {
        return new TypedReturnNode(expression, NullSource.INSTANCE);
    }

    public static TypedStringLiteralNode string(String value) {
        return new TypedStringLiteralNode(value, NullSource.INSTANCE);
    }

    public static TypedTypeLevelExpressionNode typeLevelReference(String name, TypeLevelValue value) {
        return new TypedTypeLevelReferenceNode(name, value, NullSource.INSTANCE);
    }

    public static TypedTypeLevelExpressionNode typeLevelBool() {
        return typeLevelReference("Bool", Types.BOOL);
    }

    public static TypedTypeLevelExpressionNode typeLevelInt() {
        return typeLevelReference("Int", Types.INT);
    }

    public static TypedTypeLevelExpressionNode typeLevelString() {
        return typeLevelReference("String", Types.STRING);
    }

    public static TypedVarNode var(String name, TypedExpressionNode expression) {
        return new TypedVarNode(name, expression, NullSource.INSTANCE);
    }
}
