package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Type;

public class Typed {
    public static TypedRecordFieldNode recordField(String name, Type type) {
        return new TypedRecordFieldNode(name, staticExpression(type), NullSource.INSTANCE);
    }

    public static TypedStaticExpressionNode staticExpression(Type type) {
        return new TypedStaticExpressionNode(type, NullSource.INSTANCE);
    }

    public static TypedStringLiteralNode string(String value) {
        return new TypedStringLiteralNode(value, NullSource.INSTANCE);
    }
}
