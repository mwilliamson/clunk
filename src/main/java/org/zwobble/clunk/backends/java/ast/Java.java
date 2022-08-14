package org.zwobble.clunk.backends.java.ast;

import java.util.List;
import java.util.Optional;

public class Java {
    public static JavaAddNode add(JavaExpressionNode left, JavaExpressionNode right) {
        return new JavaAddNode(left, right);
    }

    public static JavaAnnotationNode annotation(JavaTypeExpressionNode type) {
        return new JavaMarkerAnnotationNode(type);
    }

    public static JavaAnnotationNode annotation(JavaTypeExpressionNode type, JavaExpressionNode value) {
        return new JavaSingleElementAnnotation(type, value);
    }

    public static JavaBoolLiteralNode boolFalse() {
        return new JavaBoolLiteralNode(false);
    }

    public static JavaBoolLiteralNode boolTrue() {
        return new JavaBoolLiteralNode(true);
    }

    public static JavaCallNode call(JavaExpressionNode receiver, List<JavaExpressionNode> args) {
        return new JavaCallNode(receiver, args);
    }

    public static JavaCallNewNode callNew(JavaExpressionNode receiver, List<JavaExpressionNode> args) {
        return new JavaCallNewNode(receiver, Optional.empty(), args);
    }

    public static JavaConditionalBranchNode conditionalBranch(
        JavaExpressionNode condition,
        List<JavaStatementNode> body
    ) {
        return new JavaConditionalBranchNode(condition, body);
    }

    public static JavaExpressionStatementNode expressionStatement(JavaExpressionNode expression) {
        return new JavaExpressionStatementNode(expression);
    }

    public static JavaTypeExpressionNode fullyQualifiedTypeReference(String packageName, String typeName) {
        return new JavaFullyQualifiedTypeReferenceNode(packageName, typeName);
    }

    public static JavaIfStatementNode ifStatement(
        List<JavaConditionalBranchNode> conditionalBranches,
        List<JavaStatementNode> elseBody
    ) {
        return new JavaIfStatementNode(conditionalBranches, elseBody);
    }

    public static JavaImportNode importStatic(String typeName, String identifier) {
        return new JavaImportStaticNode(typeName, identifier);
    }

    public static JavaImportTypeNode importType(String typeName) {
        return new JavaImportTypeNode(typeName);
    }

    public static JavaExpressionNode intLiteral(int value) {
        return new JavaIntLiteralNode(value);
    }

    public static JavaMemberAccessNode memberAccess(JavaExpressionNode receiver, String memberName) {
        return new JavaMemberAccessNode(receiver, memberName);
    }

    public static JavaStringLiteralNode string(String value) {
        return new JavaStringLiteralNode(value);
    }

    public static JavaTypeVariableReferenceNode typeVariableReference(String name) {
        return new JavaTypeVariableReferenceNode(name);
    }

    public static JavaParamNode param(JavaTypeExpressionNode type, String name) {
        return new JavaParamNode(type, name);
    }

    public static JavaParameterizedType parameterizedType(
        JavaTypeExpressionNode receiver,
        List<JavaTypeExpressionNode> args
    ) {
        return new JavaParameterizedType(receiver, args);
    }

    public static JavaReferenceNode reference(String name) {
        return new JavaReferenceNode(name);
    }

    public static JavaReturnNode returnStatement(JavaExpressionNode expression) {
        return new JavaReturnNode(expression);
    }

    public static JavaSingleLineCommentNode singleLineComment(String value) {
        return new JavaSingleLineCommentNode(value);
    }

    public static JavaVariableDeclarationNode variableDeclaration(String name, JavaExpressionNode expression) {
        return new JavaVariableDeclarationNode(name, expression);
    }
}
