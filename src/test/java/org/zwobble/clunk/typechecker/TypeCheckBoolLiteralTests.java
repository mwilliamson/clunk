package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedBoolLiteralNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckBoolLiteralTests {
    @Test
    public void untypedFalseIsConvertedToTypedFalse() {
        var untypedNode = Untyped.boolFalse();

        var result = TypeChecker.typeCheckExpression(untypedNode, TypeCheckerContext.stub());

        assertThat(result, allOf(
            isA(TypedBoolLiteralNode.class),
            has("value", equalTo(false))
        ));
    }

    @Test
    public void untypedTrueIsConvertedToTypedTrue() {
        var untypedNode = Untyped.boolTrue();

        var result = TypeChecker.typeCheckExpression(untypedNode, TypeCheckerContext.stub());

        assertThat(result, allOf(
            isA(TypedBoolLiteralNode.class),
            has("value", equalTo(true))
        ));
    }
}
