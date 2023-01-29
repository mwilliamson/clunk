package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;
import java.util.Optional;

public class TypeScript {
    private TypeScript() {

    }

    public static TypeScriptAddNode add(TypeScriptExpressionNode left, TypeScriptExpressionNode right) {
        return new TypeScriptAddNode(left, right);
    }

    public static TypeScriptArrayNode array(List<TypeScriptExpressionNode> elements) {
        return new TypeScriptArrayNode(elements);
    }

    public static TypeScriptBoolLiteralNode boolFalse() {
        return new TypeScriptBoolLiteralNode(false);
    }

    public static TypeScriptBoolLiteralNode boolTrue() {
        return new TypeScriptBoolLiteralNode(true);
    }

    public static TypeScriptCallNode call(
        TypeScriptExpressionNode receiver,
        List<TypeScriptExpressionNode> args
    ) {
        return new TypeScriptCallNode(receiver, args);
    }

    public static TypeScriptCallNewNode callNew(
        TypeScriptExpressionNode receiver,
        List<TypeScriptExpressionNode> typeArgs,
        List<TypeScriptExpressionNode> args
    ) {
        return new TypeScriptCallNewNode(receiver, typeArgs, args);
    }

    public static TypeScriptCallNewNode callNew(
        TypeScriptExpressionNode receiver,
        List<TypeScriptExpressionNode> args
    ) {
        return new TypeScriptCallNewNode(receiver, List.of(), args);
    }

    public static TypeScriptCastNode cast(
        TypeScriptExpressionNode expression,
        TypeScriptExpressionNode type
    ) {
        return new TypeScriptCastNode(expression, type);
    }

    public static TypeScriptClassFieldNode classField(String name, TypeScriptExpressionNode type) {
        return new TypeScriptClassFieldNode(name, type, Optional.empty());
    }

    public static TypeScriptClassFieldNode classField(String name, TypeScriptExpressionNode type, TypeScriptExpressionNode value) {
        return new TypeScriptClassFieldNode(name, type, Optional.of(value));
    }

    public static TypeScriptConditionalBranchNode conditionalBranch(
        TypeScriptExpressionNode condition,
        List<TypeScriptStatementNode> body
    ) {
        return new TypeScriptConditionalBranchNode(condition, body);
    }

    public static TypeScriptExpressionNode constructedType(
        TypeScriptReferenceNode receiver,
        List<TypeScriptExpressionNode> args
    ) {
        return new TypeScriptConstructedTypeNode(receiver, args);
    }

    public static TypeScriptEqualsNode equals(TypeScriptExpressionNode left, TypeScriptExpressionNode right) {
        return new TypeScriptEqualsNode(left, right);
    }

    public static TypeScriptExportNode export(List<String> names) {
        return new TypeScriptExportNode(names);
    }

    public static TypeScriptStatementNode expressionStatement(TypeScriptExpressionNode expression) {
        return new TypeScriptExpressionStatementNode(expression);
    }

    public static TypeScriptForOfNode forOf(
        String targetName,
        TypeScriptExpressionNode iterable,
        List<TypeScriptStatementNode> body
    ) {
        return new TypeScriptForOfNode(targetName, iterable, body);
    }

    public static TypeScriptIfStatementNode ifStatement(
        List<TypeScriptConditionalBranchNode> conditionalBranches,
        List<TypeScriptStatementNode> elseBody
    ) {
        return new TypeScriptIfStatementNode(conditionalBranches, elseBody);
    }

    public static TypeScriptImportNamedNode import_(String module, List<TypeScriptImportNamedMemberNode> exports) {
        return new TypeScriptImportNamedNode(module, exports);
    }

    public static TypeScriptImportNamedMemberNode importNamedMember(String exportName, String importName) {
        return new TypeScriptImportNamedMemberNode(exportName, importName);
    }

    public static TypeScriptImportNamedMemberNode importNamedMember(String name) {
        return new TypeScriptImportNamedMemberNode(name, name);
    }

    public static TypeScriptImportNamespaceNode importNamespace(String module, String name) {
        return new TypeScriptImportNamespaceNode(module, name);
    }

    public static TypeScriptIndexNode index(
        TypeScriptExpressionNode receiver,
        TypeScriptExpressionNode index
    ) {
        return new TypeScriptIndexNode(receiver, index);
    }

    public static TypeScriptInterfaceFieldNode interfaceField(String name, TypeScriptReferenceNode type) {
        return new TypeScriptInterfaceFieldNode(name, type);
    }

    public static TypeScriptStatementNode let(String name, TypeScriptExpressionNode expression) {
        return new TypeScriptLetNode(name, expression);
    }

    public static TypeScriptLogicalAndNode logicalAnd(TypeScriptExpressionNode left, TypeScriptExpressionNode right) {
        return new TypeScriptLogicalAndNode(left, right);
    }

    public static TypeScriptLogicalNotNode logicalNot(TypeScriptExpressionNode operand) {
        return new TypeScriptLogicalNotNode(operand);
    }

    public static TypeScriptLogicalOrNode logicalOr(TypeScriptExpressionNode left, TypeScriptExpressionNode right) {
        return new TypeScriptLogicalOrNode(left, right);
    }

    public static TypeScriptModuleNode module(String name, List<TypeScriptStatementNode> statements) {
        return new TypeScriptModuleNode(name, statements);
    }

    public static TypeScriptExpressionNode numberLiteral(double value) {
        return new TypeScriptNumberLiteralNode(value);
    }

    public static TypeScriptParamNode param(String name, TypeScriptReferenceNode type) {
        return new TypeScriptParamNode(name, type);
    }

    public static TypeScriptPropertyAccessNode propertyAccess(TypeScriptReferenceNode receiver, String propertyName) {
        return new TypeScriptPropertyAccessNode(receiver, propertyName);
    }

    public static TypeScriptReferenceNode reference(String name) {
        return new TypeScriptReferenceNode(name);
    }

    public static TypeScriptReturnNode returnStatement(TypeScriptExpressionNode expression) {
        return new TypeScriptReturnNode(expression);
    }

    public static TypeScriptSingleLineCommentNode singleLineComment(String value) {
        return new TypeScriptSingleLineCommentNode(value);
    }

    public static TypeScriptStrictEqualsNode strictEquals(
        TypeScriptExpressionNode left,
        TypeScriptExpressionNode right
    ) {
        return new TypeScriptStrictEqualsNode(left, right);
    }

    public static TypeScriptStrictNotEqualNode strictNotEqual(
        TypeScriptReferenceNode left,
        TypeScriptReferenceNode right
    ) {
        return new TypeScriptStrictNotEqualNode(left, right);
    }

    public static TypeScriptStringLiteralNode string(String value) {
        return new TypeScriptStringLiteralNode(value);
    }

    public static TypeScriptSubtractNode subtract(
        TypeScriptExpressionNode left,
        TypeScriptExpressionNode right
    ) {
        return new TypeScriptSubtractNode(left, right);
    }

    public static TypeScriptSwitchCaseNode switchCase(
        TypeScriptExpressionNode expression,
        List<TypeScriptStatementNode> body
    ) {
        return new TypeScriptSwitchCaseNode(expression, body);
    }

    public static TypeScriptSwitchNode switchStatement(
        TypeScriptExpressionNode expression,
        List<TypeScriptSwitchCaseNode> cases
    ) {
        return new TypeScriptSwitchNode(expression, cases);
    }

    public static TypeScriptTypeDeclarationNode typeDeclaration(String name, TypeScriptExpressionNode value) {
        return new TypeScriptTypeDeclarationNode(name, value);
    }

    public static TypeScriptUnionNode union(List<TypeScriptExpressionNode> members) {
        return new TypeScriptUnionNode(members);
    }
}
