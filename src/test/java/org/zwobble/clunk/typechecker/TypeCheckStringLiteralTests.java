package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedStringLiteralNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckStringLiteralTests {
    @Test
    public void untypedStringIsConvertedToTypedString() {
        var untypedNode = Untyped.string("hello");

        var result = TypeChecker.typeCheckExpression(untypedNode, TypeCheckerFunctionContext.stub());

        assertThat(result, allOf(
            isA(TypedStringLiteralNode.class),
            has("value", equalTo("hello"))
        ));
    }
}
