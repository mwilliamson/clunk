package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedIntLiteralNode;

public class TypeCheckIntLiteralTests {
    @Test
    public void untypedIntIsConvertedToTypedInt() {
        var untypedNode = Untyped.intLiteral(123);

        var result = TypeChecker.typeCheckExpression(untypedNode, TypeCheckerContext.stub());

        assertThat(result, isTypedIntLiteralNode(123));
    }
}
