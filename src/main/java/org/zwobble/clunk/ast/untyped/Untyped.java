package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;

public class Untyped {
    public static UntypedArgNode arg(String name, UntypedStaticExpressionNode type) {
        return new UntypedArgNode(name, type, NullSource.INSTANCE);
    }

    public static UntypedBoolLiteralNode boolFalse() {
        return new UntypedBoolLiteralNode(false, NullSource.INSTANCE);
    }

    public static UntypedBoolLiteralNode boolTrue() {
        return new UntypedBoolLiteralNode(true, NullSource.INSTANCE);
    }

    public static UntypedRecordFieldNode recordField(String name, UntypedStaticExpressionNode type) {
        return new UntypedRecordFieldNode(name, type, NullSource.INSTANCE);
    }

    public static UntypedStaticReferenceNode staticReference(String value) {
        return new UntypedStaticReferenceNode(value, NullSource.INSTANCE);
    }

    public static UntypedStringLiteralNode string(String value) {
        return new UntypedStringLiteralNode(value, NullSource.INSTANCE);
    }
}
