package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedBoolLiteralNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class TypeCheckBoolLiteralTests {
    @Test
    public void untypedFalseIsConvertedToTypedFalse() {
        var untypedNode = Untyped.boolFalse();

        var result = TypeChecker.typeCheckExpression(untypedNode, TypeCheckerContext.stub());

        assertThat(result, instanceOf(
            TypedBoolLiteralNode.class,
            has("value", x -> x.value(), equalTo(false))
        ));
    }

    @Test
    public void untypedTrueIsConvertedToTypedTrue() {
        var untypedNode = Untyped.boolTrue();

        var result = TypeChecker.typeCheckExpression(untypedNode, TypeCheckerContext.stub());

        assertThat(result, instanceOf(
            TypedBoolLiteralNode.class,
            has("value", x -> x.value(), equalTo(true))
        ));
    }
}
