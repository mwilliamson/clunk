package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedLogicalAndNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedBoolLiteralNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckLogicalAndTests {
    @Test
    public void whenOperandsAreBoolsThenExpressionIsTypedAsLogicalOr() {
        var untypedNode = Untyped.logicalAnd(Untyped.boolFalse(), Untyped.boolTrue());
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedLogicalAndNode.class,
            has("left", isTypedBoolLiteralNode(false)),
            has("right", isTypedBoolLiteralNode(true))
        ));
    }

    @Test
    public void whenLeftOperandIsNotBoolThenErrorIsThrown() {
        var untypedNode = Untyped.logicalAnd(Untyped.intLiteral(), Untyped.boolFalse());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.BOOL));
        assertThat(result.getActual(), equalTo(Types.INT));
    }

    @Test
    public void givenLeftOperandIsBoolWhenRightOperandIsNotBoolThenErrorIsThrown() {
        var untypedNode = Untyped.logicalAnd(Untyped.boolFalse(), Untyped.intLiteral());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.BOOL));
        assertThat(result.getActual(), equalTo(Types.INT));
    }
}
