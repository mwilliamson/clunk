package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Type;

import java.util.Optional;

public class Typed {
    public static TypedBoolLiteralNode boolFalse() {
        return new TypedBoolLiteralNode(false, NullSource.INSTANCE);
    }

    public static TypedBoolLiteralNode boolTrue() {
        return new TypedBoolLiteralNode(true, NullSource.INSTANCE);
    }

    public static TypedExpressionStatementNode expressionStatement(TypedExpressionNode expression) {
        return new TypedExpressionStatementNode(expression, NullSource.INSTANCE);
    }

    public static TypedImportNode import_(NamespaceName name) {
        return new TypedImportNode(name, Optional.empty(), NullSource.INSTANCE);
    }

    public static TypedImportNode import_(NamespaceName name, String fieldName) {
        return new TypedImportNode(name, Optional.of(fieldName), NullSource.INSTANCE);
    }

    public static TypedExpressionNode intLiteral(int value) {
        return new TypedIntLiteralNode(value, NullSource.INSTANCE);
    }

    public static TypedParamNode param(String name, Type type) {
        return new TypedParamNode(name, staticExpression(type), NullSource.INSTANCE);
    }

    public static TypedRecordFieldNode recordField(String name, Type type) {
        return new TypedRecordFieldNode(name, staticExpression(type), NullSource.INSTANCE);
    }

    public static TypedReturnNode returnStatement(TypedExpressionNode expression) {
        return new TypedReturnNode(expression, NullSource.INSTANCE);
    }

    public static TypedStaticExpressionNode staticExpression(Type type) {
        return new TypedStaticExpressionNode(type, NullSource.INSTANCE);
    }

    public static TypedStringLiteralNode string(String value) {
        return new TypedStringLiteralNode(value, NullSource.INSTANCE);
    }

    public static TypedVarNode var(String name, TypedExpressionNode expression) {
        return new TypedVarNode(name, expression, NullSource.INSTANCE);
    }

    public static TypedReferenceNode reference(String name, Type type) {
        return new TypedReferenceNode(name, type, NullSource.INSTANCE);
    }
}
