package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIntAddNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.Types;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedIntLiteralNode;
import static org.zwobble.precisely.Matchers.*;

public class TypeCheckAddTests {
    @Test
    public void whenOperandsAreIntsThenExpressionIsTypedAsIntAdd() {
        var untypedNode = Untyped.add(Untyped.intLiteral(1), Untyped.intLiteral(2));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedIntAddNode.class,
            has("left", x -> x.left(), isTypedIntLiteralNode(1)),
            has("right", x -> x.right(), isTypedIntLiteralNode(2))
        ));
    }

    @Test
    public void whenLeftOperandIsNotIntThenErrorIsThrown() {
        var untypedNode = Untyped.add(Untyped.boolFalse(), Untyped.intLiteral());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.INT));
        assertThat(result.getActual(), equalTo(Types.BOOL));
    }

    @Test
    public void givenLeftOperandIsIntWhenRightOperandIsNotIntThenErrorIsThrown() {
        var untypedNode = Untyped.add(Untyped.intLiteral(), Untyped.boolFalse());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.INT));
        assertThat(result.getActual(), equalTo(Types.BOOL));
    }
}
