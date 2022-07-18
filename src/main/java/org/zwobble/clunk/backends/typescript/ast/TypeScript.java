package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;
import java.util.Optional;

public class TypeScript {
    private TypeScript() {

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
        List<TypeScriptExpressionNode> args
    ) {
        return new TypeScriptCallNewNode(receiver, args);
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

    public static TypeScriptStatementNode expressionStatement(TypeScriptExpressionNode expression) {
        return new TypeScriptExpressionStatementNode(expression);
    }

    public static TypeScriptIfStatementNode ifStatement(
        List<TypeScriptConditionalBranchNode> conditionalBranches,
        List<TypeScriptStatementNode> elseBody
    ) {
        return new TypeScriptIfStatementNode(conditionalBranches, elseBody);
    }

    public static TypeScriptImportNode import_(String module, List<String> exports) {
        return new TypeScriptImportNode(module, exports);
    }

    public static TypeScriptInterfaceFieldNode interfaceField(String name, TypeScriptReferenceNode type) {
        return new TypeScriptInterfaceFieldNode(name, type);
    }

    public static TypeScriptStatementNode let(String name, TypeScriptExpressionNode expression) {
        return new TypeScriptLetNode(name, expression);
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

    public static TypeScriptStringLiteralNode string(String value) {
        return new TypeScriptStringLiteralNode(value);
    }

    public static TypeScriptTypeDeclarationNode typeDeclaration(String name, TypeScriptExpressionNode value) {
        return new TypeScriptTypeDeclarationNode(name, value);
    }

    public static TypeScriptUnionNode union(List<TypeScriptExpressionNode> members) {
        return new TypeScriptUnionNode(members);
    }
}
