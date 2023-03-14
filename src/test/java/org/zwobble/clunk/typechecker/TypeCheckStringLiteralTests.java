package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedStringLiteralNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class TypeCheckStringLiteralTests {
    @Test
    public void untypedStringIsConvertedToTypedString() {
        var untypedNode = Untyped.string("hello");

        var result = TypeChecker.typeCheckExpression(untypedNode, TypeCheckerContext.stub());

        assertThat(result, instanceOf(
            TypedStringLiteralNode.class,
            has("value", x -> x.value(), equalTo("hello"))
        ));
    }
}
