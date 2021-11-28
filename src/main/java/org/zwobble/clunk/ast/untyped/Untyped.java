package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;

public class Untyped {
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
