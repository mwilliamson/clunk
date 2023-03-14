package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedLogicalOrNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.Types;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedBoolLiteralNode;
import static org.zwobble.precisely.Matchers.instanceOf;
import static org.zwobble.precisely.Matchers.has;

public class TypeCheckLogicalOrTests {
    @Test
    public void whenOperandsAreBoolsThenExpressionIsTypedAsLogicalOr() {
        var untypedNode = Untyped.logicalOr(Untyped.boolFalse(), Untyped.boolTrue());
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedLogicalOrNode.class,
            has("left", x -> x.left(), isTypedBoolLiteralNode(false)),
            has("right", x -> x.right(), isTypedBoolLiteralNode(true))
        ));
    }

    @Test
    public void whenLeftOperandIsNotBoolThenErrorIsThrown() {
        var untypedNode = Untyped.logicalOr(Untyped.intLiteral(), Untyped.boolFalse());
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
        var untypedNode = Untyped.logicalOr(Untyped.boolFalse(), Untyped.intLiteral());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.BOOL));
        assertThat(result.getActual(), equalTo(Types.INT));
    }
}
