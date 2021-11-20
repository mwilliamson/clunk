package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Type;

public class Typed {
    public static TypedStaticExpressionNode staticExpression(Type type) {
        return new TypedStaticExpressionNode(type, NullSource.INSTANCE);
    }
}
