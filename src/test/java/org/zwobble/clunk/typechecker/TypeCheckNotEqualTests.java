package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIntNotEqualNode;
import org.zwobble.clunk.ast.typed.TypedStringNotEqualNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.Types;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedIntLiteralNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedStringLiteralNode;
import static org.zwobble.precisely.Matchers.instanceOf;
import static org.zwobble.precisely.Matchers.has;

public class TypeCheckNotEqualTests {
    @Test
    public void whenOperandsAreIntsThenExpressionIsTypedAsIntNotEqual() {
        var untypedNode = Untyped.notEqual(Untyped.intLiteral(42), Untyped.intLiteral(47));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedIntNotEqualNode.class,
            has("left", x -> x.left(), isTypedIntLiteralNode(42)),
            has("right", x -> x.right(), isTypedIntLiteralNode(47))
        ));
    }

    @Test
    public void whenOperandsAreStringsThenExpressionIsTypedAsStringNotEqual() {
        var untypedNode = Untyped.notEqual(Untyped.string("a"), Untyped.string("b"));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedStringNotEqualNode.class,
            has("left", x -> x.left(), isTypedStringLiteralNode("a")),
            has("right", x -> x.right(), isTypedStringLiteralNode("b"))
        ));
    }

    @Test
    public void whenLeftOperandIsNotStringThenErrorIsThrown() {
        var untypedNode = Untyped.notEqual(Untyped.boolFalse(), Untyped.string());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.STRING));
        assertThat(result.getActual(), equalTo(Types.BOOL));
    }

    @Test
    public void givenLeftOperandIsStringWhenRightOperandIsNotStringThenErrorIsThrown() {
        var untypedNode = Untyped.notEqual(Untyped.string(), Untyped.boolFalse());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.STRING));
        assertThat(result.getActual(), equalTo(Types.BOOL));
    }
}
