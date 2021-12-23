package org.zwobble.clunk.backends.java.ast;

public class Java {
    public static JavaBoolLiteralNode boolFalse() {
        return new JavaBoolLiteralNode(false);
    }

    public static JavaBoolLiteralNode boolTrue() {
        return new JavaBoolLiteralNode(true);
    }

    public static JavaStringLiteralNode string(String value) {
        return new JavaStringLiteralNode(value);
    }

    public static JavaTypeReferenceNode typeReference(String name) {
        return new JavaTypeReferenceNode(name);
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
