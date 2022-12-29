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

    public static TypedCallConstructorNode callConstructor(
        TypedExpressionNode receiver,
        List<TypedTypeLevelExpressionNode> typeArgs,
        List<TypedExpressionNode> args,
        Type type
    ) {
        return new TypedCallConstructorNode(
            receiver,
            Optional.of(typeArgs),
            args,
            type,
            NullSource.INSTANCE
        );
    }

    public static TypedCallConstructorNode callConstructor(
        TypedExpressionNode receiver,
        List<TypedExpressionNode> args,
        Type type
    ) {
        return new TypedCallConstructorNode(receiver, Optional.empty(), args, type, NullSource.INSTANCE);
    }

    public static TypedCallMethodNode callMethod(
        TypedExpressionNode receiver,
        String methodName,
        List<TypedExpressionNode> args,
        Type type
    ) {
        return new TypedCallMethodNode(
            receiver,
            methodName,
            args,
            type,
            NullSource.INSTANCE
        );
    }

    public static TypedCallStaticFunctionNode callStatic(TypedExpressionNode receiver, List<TypedExpressionNode> args) {
        var staticFunctionType = (StaticFunctionType) receiver.type();
        return new TypedCallStaticFunctionNode(receiver, args, staticFunctionType, NullSource.INSTANCE);
    }

    public static TypedCastUnsafeNode castUnsafe(
        TypedExpressionNode expression,
        TypedTypeLevelExpressionNode typeExpression,
        Type type
    ) {
        return new TypedCastUnsafeNode(expression, typeExpression, type, NullSource.INSTANCE);
    }

    public static TypedConditionalBranchNode conditionalBranch(
        TypedExpressionNode condition,
        List<TypedFunctionStatementNode> body
    ) {
        return new TypedConditionalBranchNode(condition, body, NullSource.INSTANCE);
    }

    public static TypedConstructedTypeNode constructedTypeInvariant(
        TypedTypeLevelExpressionNode receiver,
        List<TypedTypeLevelExpressionNode> args,
        TypeLevelValue value
    ) {
        return constructedType(
            receiver,
            args.stream().map(arg -> TypedConstructedTypeNode.Arg.invariant(arg)).toList(),
            value
        );
    }

    public static TypedConstructedTypeNode constructedType(
        TypedTypeLevelExpressionNode receiver,
        List<TypedConstructedTypeNode.Arg> args,
        TypeLevelValue value
    ) {
        return new TypedConstructedTypeNode(
            receiver,
            args,
            value,
            NullSource.INSTANCE
        );
    }

    public static TypedConstructedTypeNode.Arg covariant(TypedTypeLevelExpressionNode type) {
        return TypedConstructedTypeNode.Arg.covariant(type);
    }

    public static TypedExpressionStatementNode expressionStatement(TypedExpressionNode expression) {
        return new TypedExpressionStatementNode(expression, NullSource.INSTANCE);
    }

    public static TypedForEachNode forEach(
        String targetName,
        Type targetType,
        TypedExpressionNode iterable,
        List<TypedFunctionStatementNode> body
    ) {
        return new TypedForEachNode(targetName, targetType, iterable, body, NullSource.INSTANCE);
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

    public static TypedExpressionNode instanceOf(
        TypedExpressionNode expression,
        TypedTypeLevelExpressionNode typeExpression
    ) {
        return new TypedInstanceOfNode(expression, typeExpression, NullSource.INSTANCE);
    }

    public static TypedInterfaceNode interface_(String name, InterfaceType type) {
        return new TypedInterfaceNode(name, type, NullSource.INSTANCE);
    }

    public static TypedIntAddNode intAdd(TypedExpressionNode left, TypedExpressionNode right) {
        return new TypedIntAddNode(left, right, NullSource.INSTANCE);
    }

    public static TypedIntEqualsNode intEquals(TypedExpressionNode left, TypedExpressionNode right) {
        return new TypedIntEqualsNode(left, right, NullSource.INSTANCE);
    }

    public static TypedExpressionNode intLiteral(int value) {
        return new TypedIntLiteralNode(value, NullSource.INSTANCE);
    }

    public static TypedListLiteralNode listLiteral(List<TypedExpressionNode> elements, Type elementType) {
        return new TypedListLiteralNode(elements, elementType, NullSource.INSTANCE);
    }

    public static TypedLocalReferenceNode localReference(String name, Type type) {
        return new TypedLocalReferenceNode(name, type, NullSource.INSTANCE);
    }

    public static TypedLogicalAndNode logicalAnd(TypedExpressionNode left, TypedExpressionNode right) {
        return new TypedLogicalAndNode(left, right, NullSource.INSTANCE);
    }

    public static TypedLogicalNotNode logicalNot(TypedExpressionNode operand) {
        return new TypedLogicalNotNode(operand, NullSource.INSTANCE);
    }

    public static TypedLogicalOrNode logicalOr(TypedExpressionNode left, TypedExpressionNode right) {
        return new TypedLogicalOrNode(left, right, NullSource.INSTANCE);
    }

    public static TypedMemberAccessNode memberAccess(
        TypedLocalReferenceNode receiver,
        String memberName,
        Type type
    ) {
        return new TypedMemberAccessNode(
            receiver,
            memberName,
            type,
            NullSource.INSTANCE,
            NullSource.INSTANCE
        );
    }

    public static TypedMemberReferenceNode memberReference(String name, Type type) {
        return new TypedMemberReferenceNode(name, type, NullSource.INSTANCE);
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

    public static TypedReturnNode returnStatement(TypedExpressionNode expression) {
        return new TypedReturnNode(expression, NullSource.INSTANCE);
    }

    public static TypedSingleLineCommentNode singleLineComment(String value) {
        return new TypedSingleLineCommentNode(value, NullSource.INSTANCE);
    }

    public static TypedStringLiteralNode string(String value) {
        return new TypedStringLiteralNode(value, NullSource.INSTANCE);
    }

    public static TypedStringEqualsNode stringEquals(TypedExpressionNode left, TypedExpressionNode right) {
        return new TypedStringEqualsNode(left, right, NullSource.INSTANCE);
    }

    public static TypedStringNotEqualNode stringNotEqual(TypedLocalReferenceNode left, TypedLocalReferenceNode right) {
        return new TypedStringNotEqualNode(left, right, NullSource.INSTANCE);
    }

    public static TypedSwitchCaseNode switchCase(
        TypedTypeLevelExpressionNode type,
        List<TypedFunctionStatementNode> body
    ) {
        return new TypedSwitchCaseNode(type, body, NullSource.INSTANCE);
    }

    public static TypedSwitchNode switchStatement(
        TypedReferenceNode expression,
        List<TypedSwitchCaseNode> cases
    ) {
        return new TypedSwitchNode(expression, cases, Optional.empty(), NullSource.INSTANCE);
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

    public static TypedTypeNarrowNode typeNarrow(String variableName, StructuredType type) {
        return new TypedTypeNarrowNode(variableName, type, NullSource.INSTANCE);
    }

    public static TypedVarNode var(String name, TypedExpressionNode expression) {
        return new TypedVarNode(name, expression, NullSource.INSTANCE);
    }
}
