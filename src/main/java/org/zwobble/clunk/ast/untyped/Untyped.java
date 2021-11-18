package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;

public class Untyped {
    public static UntypedStaticReferenceNode staticReference(String value) {
        return new UntypedStaticReferenceNode(value, NullSource.INSTANCE);
    }
}
