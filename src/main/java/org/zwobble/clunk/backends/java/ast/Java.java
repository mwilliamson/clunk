package org.zwobble.clunk.backends.java.ast;

public class Java {
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

    public static JavaExpressionStatementNode expressionStatement(JavaExpressionNode expression) {
        return new JavaExpressionStatementNode(expression);
    }

    public static JavaTypeExpressionNode fullyQualifiedTypeReference(String packageName, String typeName) {
        return new JavaFullyQualifiedTypeReferenceNode(packageName, typeName);
    }

    public static JavaExpressionNode intLiteral(int value) {
        return new JavaIntLiteralNode(value);
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

    public static JavaReferenceNode reference(String name) {
        return new JavaReferenceNode(name);
    }

    public static JavaReturnNode returnStatement(JavaExpressionNode expression) {
        return new JavaReturnNode(expression);
    }

    public static JavaVariableDeclarationNode variableDeclaration(String name, JavaExpressionNode expression) {
        return new JavaVariableDeclarationNode(name, expression);
    }
}
